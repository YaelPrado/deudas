package com.personal.deudas.data.remote.api

import com.personal.deudas.data.remote.dto.AbonoRequestDto
import com.personal.deudas.data.remote.dto.DeudaDto
import com.personal.deudas.data.remote.dto.DeudaRequestDto
import com.personal.deudas.data.remote.dto.PersonaDto
import com.personal.deudas.data.remote.dto.PersonaRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Contrato de la API con Retrofit: una función por endpoint.
 * Estas rutas exigen el token de acceso, que agrega el interceptor de OkHttp.
 */
interface DeudasApi {
    @GET("api/personas")
    suspend fun personas(): List<PersonaDto>

    @POST("api/personas")
    suspend fun crearPersona(@Body body: PersonaRequestDto): PersonaDto

    @GET("api/deudas")
    suspend fun deudas(): List<DeudaDto>

    @GET("api/deudas/{id}")
    suspend fun deuda(@Path("id") id: String): DeudaDto

    @POST("api/deudas")
    suspend fun crearDeuda(@Body body: DeudaRequestDto): DeudaDto

    @PUT("api/deudas/{id}")
    suspend fun editarDeuda(@Path("id") id: String, @Body body: DeudaRequestDto): DeudaDto

    @DELETE("api/deudas/{id}")
    suspend fun eliminarDeuda(@Path("id") id: String)

    @POST("api/deudas/{id}/abonos")
    suspend fun registrarAbono(
        @Path("id") id: String,
        @Body body: AbonoRequestDto,
    ): DeudaDto

    @DELETE("api/deudas/{id}/abonos/{abonoId}")
    suspend fun deshacerAbono(
        @Path("id") id: String,
        @Path("abonoId") abonoId: String,
    ): DeudaDto

    @POST("api/deudas/{id}/liquidar")
    suspend fun liquidar(@Path("id") id: String): DeudaDto
}
