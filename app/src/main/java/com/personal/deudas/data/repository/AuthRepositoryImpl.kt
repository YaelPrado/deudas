package com.personal.deudas.data.repository

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json
import com.personal.deudas.data.local.SessionStorage
import com.personal.deudas.data.mapper.toModel
import com.personal.deudas.data.remote.api.AuthApi
import com.personal.deudas.data.remote.dto.LoginRequestDto
import com.personal.deudas.data.remote.dto.RefrescarRequestDto
import com.personal.deudas.data.remote.dto.RegistroRequestDto

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val session: SessionStorage,
    private val json: Json,
) : AuthRepository {

    override val hasSession: StateFlow<Boolean> = session.hasSession

    override suspend fun login(correo: String, contrasena: String): Result<Unit> =
        safeApiCall(json) { authApi.login(LoginRequestDto(correo.trim(), contrasena)) }
            .map { session.save(it.toModel()) }

    override suspend fun register(nombre: String, correo: String, contrasena: String): Result<Unit> =
        safeApiCall(json) { authApi.registro(RegistroRequestDto(nombre.trim(), correo.trim(), contrasena)) }
            .map { session.save(it.toModel()) }

    override suspend fun logout() {
        val refresh = session.getRefreshToken()
        try {
            if (refresh != null) {
                // Se intenta avisar al servidor, pero sin esperar demasiado si no hay red.
                withTimeoutOrNull(TIEMPO_MAXIMO_LOGOUT_MS) {
                    safeApiCall(json) { authApi.logout(RefrescarRequestDto(refresh)) }
                }
            }
        } finally {
            session.clear()
        }
    }

    private companion object {
        const val TIEMPO_MAXIMO_LOGOUT_MS = 3_000L
    }
}
