package com.personal.deudas.data.repository

import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.json.Json
import com.personal.deudas.data.model.ApiException
import com.personal.deudas.data.model.NetworkException
import com.personal.deudas.data.model.UnexpectedException
import com.personal.deudas.data.remote.dto.ErrorResponseDto
import retrofit2.HttpException

/**
 * Ejecuta una llamada a la API y convierte cualquier falla en un [Result] con un error de la app:
 *  - respuesta de error de la API  -> [ApiException] (con el código y el mensaje de la API)
 *  - sin conexión o tiempo agotado -> [NetworkException]
 *  - cualquier otra cosa           -> [UnexpectedException]
 * Así los ViewModels nunca ven excepciones de Retrofit ni de OkHttp.
 */
internal suspend fun <T> safeApiCall(json: Json, block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e // cancelar una corrutina no es un error
    } catch (e: HttpException) {
        Result.failure(e.toApiException(json))
    } catch (e: IOException) {
        Result.failure(NetworkException(e))
    } catch (e: Exception) {
        Result.failure(UnexpectedException(e))
    }

private fun HttpException.toApiException(json: Json): ApiException {
    val texto = try {
        response()?.errorBody()?.string()
    } catch (e: IOException) {
        null
    }
    val detalle = texto?.let {
        try {
            json.decodeFromString<ErrorResponseDto>(it).error
        } catch (e: Exception) {
            null
        }
    }
    return ApiException(
        httpCode = code(),
        codigo = detalle?.codigo ?: "ERROR_${code()}",
        message = detalle?.mensaje ?: "El servidor respondió con un error (${code()}).",
    )
}
