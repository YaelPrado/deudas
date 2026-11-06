package com.personal.deudas.data.remote.api

import com.personal.deudas.data.remote.dto.LoginRequestDto
import com.personal.deudas.data.remote.dto.RefrescarRequestDto
import com.personal.deudas.data.remote.dto.RegistroRequestDto
import com.personal.deudas.data.remote.dto.SesionDto
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Rutas de sesión. Usan un cliente HTTP "limpio" (sin interceptor de token ni autenticador),
 * porque ninguna necesita el token de acceso y así la renovación no puede entrar en un ciclo.
 */
interface AuthApi {
    @POST("api/auth/registro")
    suspend fun registro(@Body body: RegistroRequestDto): SesionDto

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequestDto): SesionDto

    @POST("api/auth/refrescar")
    suspend fun refrescar(@Body body: RefrescarRequestDto): SesionDto

    @POST("api/auth/logout")
    suspend fun logout(@Body body: RefrescarRequestDto)
}
