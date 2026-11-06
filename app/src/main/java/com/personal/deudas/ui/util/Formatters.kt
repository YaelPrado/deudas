package com.personal.deudas.ui.util

import java.math.BigDecimal
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.personal.deudas.data.model.AppException
import com.personal.deudas.data.model.EstadoDeuda
import com.personal.deudas.data.model.TipoDeuda

private val LOCALE_MX = Locale.forLanguageTag("es-MX")

fun BigDecimal.toMoneyText(): String =
    NumberFormat.getCurrencyInstance(LOCALE_MX).format(this)

private val FORMATO_FECHA: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", LOCALE_MX)

fun LocalDate.toDateText(): String = format(FORMATO_FECHA)

fun Instant.toDateText(zone: ZoneId = ZoneId.systemDefault()): String =
    atZone(zone).toLocalDate().toDateText()

/** "1500.50" para rellenar el campo de monto al editar (sin símbolo ni separadores). */
fun BigDecimal.toInputText(): String = setScale(2, java.math.RoundingMode.HALF_UP).toPlainString()

val TipoDeuda.label: String
    get() = when (this) {
        TipoDeuda.ME_DEBEN -> "Me deben"
        TipoDeuda.DEBO -> "Debo"
    }

val EstadoDeuda.label: String
    get() = when (this) {
        EstadoDeuda.PENDIENTE -> "Pendiente"
        EstadoDeuda.PARCIAL -> "Parcial"
        EstadoDeuda.PAGADA -> "Pagada"
    }

/** Mensaje listo para mostrar al usuario a partir de cualquier error. */
fun Throwable.userMessage(): String =
    (this as? AppException)?.message ?: "Ocurrió un error inesperado. Inténtalo de nuevo."
