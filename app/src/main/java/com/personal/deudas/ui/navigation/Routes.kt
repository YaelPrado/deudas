package com.personal.deudas.ui.navigation

object Routes {
    const val ARG_DEUDA_ID = "deudaId"

    /** Clave en el SavedStateHandle de la pantalla anterior para pasarle un mensaje (snackbar). */
    const val KEY_MENSAJE = "mensaje"

    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val DETAIL = "deuda/{$ARG_DEUDA_ID}"
    const val FORM = "form?$ARG_DEUDA_ID={$ARG_DEUDA_ID}"

    fun detail(id: String) = "deuda/$id"
    fun formNueva() = "form"
    fun formEditar(id: String) = "form?$ARG_DEUDA_ID=$id"
}
