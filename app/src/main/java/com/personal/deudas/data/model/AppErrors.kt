package com.personal.deudas.data.model

/**
 * Errores que los repositorios entregan a los ViewModels (dentro de un Result).
 * Todos traen un mensaje ya listo para mostrar al usuario, en español.
 */
sealed class AppException(message: String, cause: Throwable? = null) : Exception(message, cause)

/** La API respondió con un error. [codigo] es el código estable de la API (por ejemplo ABONO_EXCEDE_SALDO). */
class ApiException(
    val httpCode: Int,
    val codigo: String,
    message: String,
) : AppException(message)

/** No se pudo llegar al servidor (sin internet, servidor apagado, tiempo agotado). */
class NetworkException(cause: Throwable? = null) :
    AppException("No hay conexión con el servidor. Revisa tu internet e inténtalo de nuevo.", cause)

/** Cualquier otra falla inesperada (por ejemplo, una respuesta con formato desconocido). */
class UnexpectedException(cause: Throwable? = null) :
    AppException("Ocurrió un error inesperado. Inténtalo de nuevo.", cause)

/** Códigos de error de la API que la app necesita reconocer. */
object ApiCodes {
    const val TOKEN_EXPIRADO = "TOKEN_EXPIRADO"
    const val TOKEN_INVALIDO = "TOKEN_INVALIDO"
    const val NO_AUTENTICADO = "NO_AUTENTICADO"
    const val SESION_CERRADA = "SESION_CERRADA"
    const val REFRESCO_INVALIDO = "REFRESCO_INVALIDO"
    const val CREDENCIALES_INVALIDAS = "CREDENCIALES_INVALIDAS"

    /** Códigos que significan "esta sesión ya no sirve": hay que volver al login. */
    val SESION_PERDIDA = setOf(TOKEN_INVALIDO, NO_AUTENTICADO, SESION_CERRADA, REFRESCO_INVALIDO)
}
