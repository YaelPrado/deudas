package com.personal.deudas.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.personal.deudas.data.model.Session
import com.personal.deudas.data.model.User

/** Guarda la sesión en SharedPreferences privadas de la app (sin respaldo en la nube: ver el manifiesto). */
class SharedPreferencesSessionStorage(context: Context) : SessionStorage {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(NOMBRE_ARCHIVO, Context.MODE_PRIVATE)

    private val _hasSession = MutableStateFlow(prefs.getString(KEY_ACCESS, null) != null)
    override val hasSession: StateFlow<Boolean> = _hasSession.asStateFlow()

    override fun getAccessToken(): String? = prefs.getString(KEY_ACCESS, null)

    override fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH, null)

    override fun getUser(): User? {
        val id = prefs.getString(KEY_USER_ID, null) ?: return null
        return User(
            id = id,
            nombre = prefs.getString(KEY_USER_NOMBRE, "").orEmpty(),
            correo = prefs.getString(KEY_USER_CORREO, "").orEmpty(),
            rol = prefs.getString(KEY_USER_ROL, "USUARIO").orEmpty(),
        )
    }

    override fun save(session: Session) {
        prefs.edit()
            .putString(KEY_ACCESS, session.accessToken)
            .putString(KEY_REFRESH, session.refreshToken)
            .putString(KEY_USER_ID, session.user.id)
            .putString(KEY_USER_NOMBRE, session.user.nombre)
            .putString(KEY_USER_CORREO, session.user.correo)
            .putString(KEY_USER_ROL, session.user.rol)
            .apply()
        _hasSession.value = true
    }

    override fun updateTokens(accessToken: String, refreshToken: String) {
        prefs.edit()
            .putString(KEY_ACCESS, accessToken)
            .putString(KEY_REFRESH, refreshToken)
            .apply()
    }

    override fun clear() {
        prefs.edit().clear().apply()
        _hasSession.value = false
    }

    private companion object {
        const val NOMBRE_ARCHIVO = "sesion"
        const val KEY_ACCESS = "access_token"
        const val KEY_REFRESH = "refresh_token"
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_NOMBRE = "user_nombre"
        const val KEY_USER_CORREO = "user_correo"
        const val KEY_USER_ROL = "user_rol"
    }
}
