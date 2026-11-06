package com.personal.deudas.ui.util

import java.math.BigDecimal

/** Validaciones de formularios. Funciones puras: devuelven el mensaje de error o null si el dato es válido. */
object Validators {
    private val CORREO = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$")
    private val MONTO = Regex("^\\d+(\\.\\d{1,2})?$")

    const val MIN_CONTRASENA = 8
    const val MAX_CONTRASENA_BYTES = 72
    const val MAX_NOMBRE = 50
    const val MAX_CONCEPTO = 100
    const val MAX_TELEFONO = 20
    const val MAX_NOTA = 200
    val MONTO_MAXIMO: BigDecimal = BigDecimal("10000000.00")

    fun correo(valor: String): String? {
        val t = valor.trim()
        return when {
            t.isEmpty() -> "Escribe tu correo."
            !CORREO.matches(t) -> "Escribe un correo válido."
            else -> null
        }
    }

    fun contrasenaLogin(valor: String): String? =
        if (valor.isEmpty()) "Escribe tu contraseña." else null

    fun contrasenaNueva(valor: String): String? = when {
        valor.isEmpty() -> "Escribe una contraseña."
        valor.length < MIN_CONTRASENA -> "Usa al menos $MIN_CONTRASENA caracteres."
        valor.toByteArray(Charsets.UTF_8).size > MAX_CONTRASENA_BYTES -> "La contraseña es demasiado larga."
        else -> null
    }

    fun confirmacion(contrasena: String, confirmacion: String): String? = when {
        confirmacion.isEmpty() -> "Confirma tu contraseña."
        confirmacion != contrasena -> "Las contraseñas no coinciden."
        else -> null
    }

    fun nombre(valor: String): String? {
        val t = valor.trim()
        return when {
            t.isEmpty() -> "Escribe un nombre."
            t.length > MAX_NOMBRE -> "Máximo $MAX_NOMBRE caracteres."
            else -> null
        }
    }

    fun telefono(valor: String): String? =
        if (valor.trim().length > MAX_TELEFONO) "Máximo $MAX_TELEFONO caracteres." else null

    fun concepto(valor: String): String? {
        val t = valor.trim()
        return when {
            t.isEmpty() -> "Escribe el concepto."
            t.length > MAX_CONCEPTO -> "Máximo $MAX_CONCEPTO caracteres."
            else -> null
        }
    }

    fun nota(valor: String): String? =
        if (valor.trim().length > MAX_NOTA) "Máximo $MAX_NOTA caracteres." else null

    fun persona(personaId: String?): String? =
        if (personaId.isNullOrEmpty()) "Elige una persona." else null

    /** Convierte el texto capturado a pesos. Acepta "1500", "1500.5", "1,500.50". Devuelve null si no es un número válido. */
    fun parseMonto(valor: String): BigDecimal? {
        val limpio = valor.trim().replace(",", "").replace(" ", "")
        if (!MONTO.matches(limpio)) return null
        return BigDecimal(limpio)
    }

    fun monto(valor: String): String? {
        if (valor.isBlank()) return "Escribe el monto."
        val monto = parseMonto(valor) ?: return "Escribe un monto válido, con máximo 2 decimales."
        return when {
            monto.signum() <= 0 -> "El monto debe ser mayor a cero."
            monto > MONTO_MAXIMO -> "El monto máximo es \$10,000,000.00."
            else -> null
        }
    }
}
