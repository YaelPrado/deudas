package com.personal.deudas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.math.BigDecimal
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.personal.deudas.data.model.Deuda
import com.personal.deudas.data.model.EstadoDeuda
import com.personal.deudas.data.model.TipoDeuda
import com.personal.deudas.data.repository.AuthRepository
import com.personal.deudas.data.repository.DeudaRepository
import com.personal.deudas.ui.state.HomeUiState
import com.personal.deudas.ui.state.toItemUi
import com.personal.deudas.ui.util.toMoneyText
import com.personal.deudas.ui.util.userMessage

class HomeViewModel(
    private val deudas: DeudaRepository,
    private val auth: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private var todas: List<Deuda> = emptyList()
    private var cargadaAlMenosUnaVez = false
    private var loadJob: Job? = null

    init {
        cargar()
    }

    /**
     * Carga la lista. Con [silenciosa] = true (al volver a la pantalla) no se muestra el indicador
     * de carga ni se pierde la lista actual si la petición falla.
     */
    fun cargar(silenciosa: Boolean = false) {
        if (loadJob?.isActive == true) return
        if (!silenciosa || !cargadaAlMenosUnaVez) {
            _state.update { it.copy(isLoading = true, error = null) }
        }
        loadJob = viewModelScope.launch {
            deudas.listar()
                .onSuccess {
                    todas = it
                    cargadaAlMenosUnaVez = true
                    _state.update { s -> s.copy(isLoading = false, error = null).conLista() }
                }
                .onFailure { e ->
                    if (cargadaAlMenosUnaVez) {
                        // Se conserva la lista que ya se tenía y solo se avisa.
                        _state.update { it.copy(isLoading = false, message = if (silenciosa) null else e.userMessage()) }
                    } else {
                        _state.update { it.copy(isLoading = false, error = e.userMessage()) }
                    }
                }
        }
    }

    fun onTipoFiltro(tipo: TipoDeuda?) = _state.update { it.copy(tipoFiltro = tipo).conLista() }

    fun onEstadoFiltro(estado: EstadoDeuda?) = _state.update { it.copy(estadoFiltro = estado).conLista() }

    fun onMessageShown() = _state.update { it.copy(message = null) }

    fun onLogout() {
        if (_state.value.isLoggingOut) return
        _state.update { it.copy(isLoggingOut = true) }
        viewModelScope.launch {
            auth.logout() // al borrar la sesión, la navegación vuelve sola al login
            _state.update { it.copy(isLoggingOut = false) }
        }
    }

    /** Recalcula lista filtrada y totales a partir de [todas]. */
    private fun HomeUiState.conLista(): HomeUiState {
        val filtradas = todas.filter { d ->
            (tipoFiltro == null || d.tipo == tipoFiltro) && (estadoFiltro == null || d.estado == estadoFiltro)
        }
        fun total(tipo: TipoDeuda): BigDecimal =
            todas.filter { it.tipo == tipo }.fold(BigDecimal.ZERO) { acc, d -> acc + d.saldo }
        return copy(
            items = filtradas.map { it.toItemUi() },
            totalDeudas = todas.size,
            meDebenTexto = total(TipoDeuda.ME_DEBEN).toMoneyText(),
            deboTexto = total(TipoDeuda.DEBO).toMoneyText(),
        )
    }
}
