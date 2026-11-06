package com.personal.deudas.ui.state

import java.time.LocalDate
import com.personal.deudas.data.model.EstadoDeuda
import com.personal.deudas.data.model.Persona
import com.personal.deudas.data.model.TipoDeuda

data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",
    val correoError: String? = null,
    val contrasenaError: String? = null,
    val isLoading: Boolean = false,
    /** Error general (credenciales, red...). */
    val error: String? = null,
)

data class RegisterUiState(
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val confirmacion: String = "",
    val nombreError: String? = null,
    val correoError: String? = null,
    val contrasenaError: String? = null,
    val confirmacionError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class HomeUiState(
    val isLoading: Boolean = true,
    /** Error que ocupa toda la pantalla (solo cuando no hay datos que mostrar). */
    val error: String? = null,
    /** Deudas ya filtradas. */
    val items: List<DeudaItemUi> = emptyList(),
    val totalDeudas: Int = 0,
    val tipoFiltro: TipoDeuda? = null,
    val estadoFiltro: EstadoDeuda? = null,
    val meDebenTexto: String = "",
    val deboTexto: String = "",
    /** Mensaje de una sola vez para el snackbar. */
    val message: String? = null,
    val isLoggingOut: Boolean = false,
)

data class AbonoDialogUi(
    val monto: String = "",
    val nota: String = "",
    val montoError: String? = null,
    val notaError: String? = null,
)

data class DeudaDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val deuda: DeudaDetalleUi? = null,
    /** Hay una operación en curso (abono, liquidar, eliminar...). */
    val isWorking: Boolean = false,
    val abonoDialog: AbonoDialogUi? = null,
    val message: String? = null,
    val eliminada: Boolean = false,
)

data class NuevaPersonaUi(
    val nombre: String = "",
    val telefono: String = "",
    val nombreError: String? = null,
    val telefonoError: String? = null,
    val guardando: Boolean = false,
    val error: String? = null,
)

data class DeudaFormUiState(
    val esEdicion: Boolean = false,
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val personas: List<Persona> = emptyList(),
    val personaId: String? = null,
    val tipo: TipoDeuda = TipoDeuda.ME_DEBEN,
    val concepto: String = "",
    val monto: String = "",
    val vencimiento: LocalDate? = null,
    val personaError: String? = null,
    val conceptoError: String? = null,
    val montoError: String? = null,
    val isSaving: Boolean = false,
    val message: String? = null,
    val guardada: Boolean = false,
    val nuevaPersona: NuevaPersonaUi? = null,
) {
    val personaNombre: String? get() = personas.firstOrNull { it.id == personaId }?.nombre
}
