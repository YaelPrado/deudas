package com.personal.deudas.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.personal.deudas.data.model.DeudaInput
import com.personal.deudas.data.model.TipoDeuda
import com.personal.deudas.data.repository.DeudaRepository
import com.personal.deudas.data.repository.PersonaRepository
import com.personal.deudas.ui.navigation.Routes
import com.personal.deudas.ui.state.DeudaFormUiState
import com.personal.deudas.ui.state.NuevaPersonaUi
import com.personal.deudas.ui.util.Validators
import com.personal.deudas.ui.util.toInputText
import com.personal.deudas.ui.util.userMessage

/** Sirve para crear (sin deudaId) y para editar (con deudaId) una deuda. */
class DeudaFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val deudas: DeudaRepository,
    private val personas: PersonaRepository,
) : ViewModel() {

    private val deudaId: String? = savedStateHandle[Routes.ARG_DEUDA_ID]

    private val _state = MutableStateFlow(DeudaFormUiState(esEdicion = deudaId != null))
    val state: StateFlow<DeudaFormUiState> = _state.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        _state.update { it.copy(isLoading = true, loadError = null) }
        viewModelScope.launch {
            val listaPersonas = personas.listar().getOrElse { e ->
                _state.update { it.copy(isLoading = false, loadError = e.userMessage()) }
                return@launch
            }
            if (deudaId == null) {
                _state.update { it.copy(isLoading = false, personas = listaPersonas) }
                return@launch
            }
            deudas.obtener(deudaId)
                .onSuccess { d ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            personas = listaPersonas,
                            personaId = d.personaId,
                            tipo = d.tipo,
                            concepto = d.concepto,
                            monto = d.montoTotal.toInputText(),
                            vencimiento = d.vencimiento,
                        )
                    }
                }
                .onFailure { e -> _state.update { it.copy(isLoading = false, loadError = e.userMessage()) } }
        }
    }

    fun onPersonaChange(id: String) = _state.update { it.copy(personaId = id, personaError = null) }

    fun onTipoChange(tipo: TipoDeuda) = _state.update { it.copy(tipo = tipo) }

    fun onConceptoChange(valor: String) = _state.update { it.copy(concepto = valor, conceptoError = null) }

    fun onMontoChange(valor: String) = _state.update { it.copy(monto = valor, montoError = null) }

    fun onVencimientoChange(fecha: LocalDate?) = _state.update { it.copy(vencimiento = fecha) }

    fun onMessageShown() = _state.update { it.copy(message = null) }

    fun onGuardar() {
        val s = _state.value
        if (s.isSaving || s.isLoading) return
        val personaError = Validators.persona(s.personaId)
        val conceptoError = Validators.concepto(s.concepto)
        val montoError = Validators.monto(s.monto)
        val monto = Validators.parseMonto(s.monto)
        if (personaError != null || conceptoError != null || montoError != null || monto == null || s.personaId == null) {
            _state.update { it.copy(personaError = personaError, conceptoError = conceptoError, montoError = montoError) }
            return
        }
        val input = DeudaInput(s.personaId, s.tipo, s.concepto.trim(), monto, s.vencimiento)
        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val resultado = if (deudaId == null) deudas.crear(input) else deudas.editar(deudaId, input)
            resultado
                .onSuccess { _state.update { it.copy(isSaving = false, guardada = true) } }
                .onFailure { e -> _state.update { it.copy(isSaving = false, message = e.userMessage()) } }
        }
    }

    // ---- Nueva persona (diálogo) ----

    fun onAbrirNuevaPersona() = _state.update { it.copy(nuevaPersona = NuevaPersonaUi()) }

    fun onCerrarNuevaPersona() = _state.update { it.copy(nuevaPersona = null) }

    fun onNuevaPersonaNombre(valor: String) =
        _state.update { s -> s.copy(nuevaPersona = s.nuevaPersona?.copy(nombre = valor, nombreError = null, error = null)) }

    fun onNuevaPersonaTelefono(valor: String) =
        _state.update { s -> s.copy(nuevaPersona = s.nuevaPersona?.copy(telefono = valor, telefonoError = null, error = null)) }

    fun onGuardarNuevaPersona() {
        val np = _state.value.nuevaPersona ?: return
        if (np.guardando) return
        val nombreError = Validators.nombre(np.nombre)
        val telefonoError = Validators.telefono(np.telefono)
        if (nombreError != null || telefonoError != null) {
            _state.update { it.copy(nuevaPersona = np.copy(nombreError = nombreError, telefonoError = telefonoError)) }
            return
        }
        _state.update { it.copy(nuevaPersona = np.copy(guardando = true, error = null)) }
        viewModelScope.launch {
            personas.crear(np.nombre.trim(), np.telefono.trim().ifEmpty { null })
                .onSuccess { nueva ->
                    _state.update {
                        it.copy(
                            personas = (it.personas + nueva).sortedBy { p -> p.nombre.lowercase() },
                            personaId = nueva.id,
                            personaError = null,
                            nuevaPersona = null,
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { s -> s.copy(nuevaPersona = s.nuevaPersona?.copy(guardando = false, error = e.userMessage())) }
                }
        }
    }
}
