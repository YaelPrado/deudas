package com.personal.deudas.data.local

import kotlinx.coroutines.flow.StateFlow
import com.personal.deudas.data.model.Session
import com.personal.deudas.data.model.User

/**
 * Abstracción de dónde se guarda la sesión. Hoy se implementa con SharedPreferences,
 * pero el resto de la app solo conoce esta interfaz (se podría cambiar a DataStore sin tocar nada más).
 */
interface SessionStorage {
    /** true mientras haya una sesión guardada. La navegación reacciona a este valor. */
    val hasSession: StateFlow<Boolean>

    fun getAccessToken(): String?

    fun getRefreshToken(): String?

    fun getUser(): User?

    /** Guarda una sesión completa (después de iniciar sesión o registrarse). */
    fun save(session: Session)

    /** Reemplaza solo los tokens (después de renovarlos). */
    fun updateTokens(accessToken: String, refreshToken: String)

    /** Borra todo: deja la app sin sesión. */
    fun clear()
}
