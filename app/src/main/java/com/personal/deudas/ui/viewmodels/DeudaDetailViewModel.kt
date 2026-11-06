package com.personal.deudas.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.personal.deudas.data.model.Deuda
import com.personal.deudas.data.repository.DeudaRepository
import com.personal.deudas.ui.navigation.Routes
import com.personal.deudas.ui.state.AbonoDialogUi
import com.personal.deudas.ui.state.DeudaDetailUiState
import com.personal.deudas.ui.state.toDetalleUi
import com.personal.deudas.ui.util.Validators
import com.personal.deudas.ui.util.toMoneyText
import com.personal.deudas.ui.util.userMessage

class DeudaDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repo: DeudaRepository,
) : ViewModel() {

    private val deudaId: String = checkNotNull(savedStateHandle[Routes.ARG_DEUDA_ID])

    private val _state = MutableStateFlow(DeudaDetailUiState())
    val state: StateFlow<DeudaDetailUiState> = _state.asStateFlow()

    private var modelo: Deuda? = null
    private var loadJob: Job? = null

    init {
        cargar()
    }

    fun cargar(silenciosa: Boolean = false) {
        if (loadJob?.isActive == true) return
        if (!silenciosa || modelo == null) _state.update { it.copy(isLoading = true, error = null) }
        loadJob = viewModelScope.launch {
            repo.obtener(deudaId)
                .onSuccess { aplicar(it) }
                .onFailure { e ->
                    if (modelo != null) {
                        _state.update { it.copy(isLoading = false, message = if (silenciosa) null else e.userMessage()) }
                    } else {
                        _state.update { it.copy(isLoading = false, error = e.userMessage()) }
                    }
                }
        }
    }

    private fun aplicar(deuda: Deuda, mensaje: String? = null) {
        modelo = deuda
        _state.update {
            it.copy(isLoading = false, error = null, deuda = deuda.toDetalleUi(), isWorking = false, message = mensaje ?: it.message)
        }
    }

    private fun fallo(e: Throwable) = _state.update { it.copy(isWorking = false, message = e.userMessage()) }

    // ---- Abono ----

    fun onAbrirAbono() = _state.update { it.copy(abonoDialog = AbonoDialogUi()) }

    fun onCerrarAbono() = _state.update { it.copy(abonoDialog = null) }

    fun onAbonoMontoChange(valor: String) =
        _state.update { s -> s.copy(abonoDialog = s.abonoDialog?.copy(monto = valor, montoError = null)) }

    fun onAbonoNotaChange(valor: String) =
        _state.update { s -> s.copy(abonoDialog = s.abonoDialog?.copy(nota = valor, notaError = null)) }

    fun onConfirmarAbono() {
        val dialogo = _state.value.abonoDialog ?: return
        val deuda = modelo ?: return
        if (_state.value.isWorking) return
        var montoError = Validators.monto(dialogo.monto)
        val monto = Validators.parseMonto(dialogo.monto)
        if (montoError == null && monto != null && monto > deuda.saldo) {
            montoError = "El abono no puede ser mayor al saldo (${deuda.saldo.toMoneyText()})."
        }
        val notaError = Validators.nota(dialogo.nota)
        if (montoError != null || notaError != null || monto == null) {
            _state.update { it.copy(abonoDialog = dialogo.copy(montoError = montoError, notaError = notaError)) }
            return
        }
        _state.update { it.copy(isWorking = true) }
        viewModelScope.launch {
            repo.registrarAbono(deuda.id, monto, dialogo.nota.trim().ifEmpty { null })
                .onSuccess {
                    aplicar(it, "Abono registrado.")
                    _state.update { s -> s.copy(abonoDialog = null) }
                }
                .onFailure { e ->
                    // El diálogo se queda abierto para corregir; el error se ve dentro del diálogo.
                    _state.update { s ->
                        s.copy(isWorking = false, abonoDialog = s.abonoDialog?.copy(montoError = e.userMessage()))
                    }
                }
        }
    }

    fun onDeshacerAbono(abonoId: String) {
        if (_state.value.isWorking) return
        _state.update { it.copy(isWorking = true) }
        viewModelScope.launch {
            repo.deshacerAbono(deudaId, abonoId)
                .onSuccess { aplicar(it, "Abono eliminado.") }
                .onFailure(::fallo)
        }
    }

    fun onLiquidar() {
        if (_state.value.isWorking) return
        _state.update { it.copy(isWorking = true) }
        viewModelScope.launch {
            repo.liquidar(deudaId)
                .onSuccess { aplicar(it, "Deuda liquidada.") }
                .onFailure(::fallo)
        }
    }

    fun onEliminar() {
        if (_state.value.isWorking) return
        _state.update { it.copy(isWorking = true) }
        viewModelScope.launch {
            repo.eliminar(deudaId)
                .onSuccess { _state.update { it.copy(isWorking = false, eliminada = true) } }
                .onFailure(::fallo)
        }
    }

    fun onMessageShown() = _state.update { it.copy(message = null) }
}
