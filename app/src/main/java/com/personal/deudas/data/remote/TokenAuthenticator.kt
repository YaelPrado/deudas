package com.personal.deudas.data.remote

import java.io.IOException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import com.personal.deudas.data.local.SessionStorage
import com.personal.deudas.data.model.ApiCodes
import com.personal.deudas.data.remote.api.AuthApi
import com.personal.deudas.data.remote.dto.ErrorResponseDto
import com.personal.deudas.data.remote.dto.RefrescarRequestDto
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.HttpException

/**
 * OkHttp la invoca solo cuando la API responde 401. Según el `codigo` del error:
 *
 *  - TOKEN_EXPIRADO: renueva los tokens con el refresh token y repite la petición una vez.
 *  - SESION_CERRADA, TOKEN_INVALIDO, NO_AUTENTICADO, REFRESCO_INVALIDO: la sesión ya no sirve;
 *    la borra. La navegación detecta que no hay sesión y regresa al login.
 *  - Cualquier otro 401 (por ejemplo, credenciales incorrectas en el login) no se toca.
 */
class TokenAuthenticator(
    private val session: SessionStorage,
    private val authApi: AuthApi,
    private val json: Json,
) : Authenticator {

    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        // Si ya reintentamos con un token nuevo y sigue fallando, no insistimos (evita ciclos).
        if (numeroDeIntentos(response) > 1) return null

        val codigo = leerCodigo(response)
        return when {
            codigo == ApiCodes.TOKEN_EXPIRADO -> synchronized(lock) { renovar(response.request) }
            codigo in ApiCodes.SESION_PERDIDA -> {
                session.clear()
                null
            }
            else -> null
        }
    }

    private fun renovar(peticion: Request): Request? {
        val tokenUsado = peticion.header("Authorization")?.removePrefix("Bearer ")
        val tokenActual = session.getAccessToken()
        // Otra petición ya renovó los tokens mientras esta esperaba: basta con reintentar.
        if (tokenActual != null && tokenActual != tokenUsado) return conToken(peticion, tokenActual)

        val refresh = session.getRefreshToken()
        if (refresh == null) {
            session.clear()
            return null
        }
        return try {
            val nueva = runBlocking { authApi.refrescar(RefrescarRequestDto(refresh)) }
            session.updateTokens(nueva.token, nueva.refreshToken)
            conToken(peticion, nueva.token)
        } catch (e: HttpException) {
            // El servidor rechazó el refresh token: la sesión terminó.
            if (e.code() == 401) session.clear()
            null
        } catch (e: IOException) {
            // Sin red: no se cierra la sesión; la petición falla y el usuario puede reintentar.
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun conToken(peticion: Request, token: String): Request =
        peticion.newBuilder().header("Authorization", "Bearer $token").build()

    private fun numeroDeIntentos(response: Response): Int {
        var intentos = 1
        var previa = response.priorResponse
        while (previa != null) {
            intentos++
            previa = previa.priorResponse
        }
        return intentos
    }

    private fun leerCodigo(response: Response): String? = try {
        val texto = response.peekBody(PESO_MAXIMO_LECTURA).string()
        json.decodeFromString<ErrorResponseDto>(texto).error.codigo
    } catch (e: Exception) {
        null
    }

    private companion object {
        const val PESO_MAXIMO_LECTURA = 4096L
    }
}
