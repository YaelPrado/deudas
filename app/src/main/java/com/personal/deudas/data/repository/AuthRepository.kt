package com.personal.deudas.data.repository

import kotlinx.coroutines.flow.StateFlow

/** Lo único que los ViewModels saben de la sesión. La implementación llama a la API y guarda los tokens. */
interface AuthRepository {
    /** true mientras haya sesión. Cambia a false al cerrar sesión o cuando el servidor la invalida. */
    val hasSession: StateFlow<Boolean>

    suspend fun login(correo: String, contrasena: String): Result<Unit>

    suspend fun register(nombre: String, correo: String, contrasena: String): Result<Unit>

    /** Cierra la sesión en el servidor (si hay red) y siempre borra la sesión local. */
    suspend fun logout()
}
