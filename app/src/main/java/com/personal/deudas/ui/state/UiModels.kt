package com.personal.deudas.ui.state

import java.time.LocalDate
import java.time.ZoneId
import com.personal.deudas.data.model.Abono
import com.personal.deudas.data.model.Deuda
import com.personal.deudas.data.model.EstadoDeuda
import com.personal.deudas.data.model.TipoDeuda
import com.personal.deudas.ui.util.toDateText
import com.personal.deudas.ui.util.toMoneyText

// Modelos listos para pintar: los ViewModels ya formatearon dinero y fechas.

data class DeudaItemUi(
    val id: String,
    val persona: String,
    val concepto: String,
    val tipo: TipoDeuda,
    val estado: EstadoDeuda,
    val saldoTexto: String,
    val totalTexto: String,
    val vencimientoTexto: String?,
    val vencida: Boolean,
    /** 0f..1f: qué parte de la deuda ya se abonó. */
    val progreso: Float,
)

data class AbonoUi(
    val id: String,
    val montoTexto: String,
    val fechaTexto: String,
    val nota: String?,
)

data class DeudaDetalleUi(
    val id: String,
    val persona: String,
    val concepto: String,
    val tipo: TipoDeuda,
    val estado: EstadoDeuda,
    val totalTexto: String,
    val abonadoTexto: String,
    val saldoTexto: String,
    val vencimientoTexto: String?,
    val vencida: Boolean,
    val creadaTexto: String,
    val progreso: Float,
    val abonos: List<AbonoUi>,
    val saldoPendiente: Boolean,
)

private fun Deuda.progreso(): Float =
    if (montoTotal.signum() <= 0) 0f
    else abonado.divide(montoTotal, 4, java.math.RoundingMode.HALF_UP).toFloat().coerceIn(0f, 1f)

private fun Deuda.estaVencida(hoy: LocalDate): Boolean =
    estado != EstadoDeuda.PAGADA && vencimiento != null && vencimiento.isBefore(hoy)

fun Deuda.toItemUi(hoy: LocalDate = LocalDate.now()) = DeudaItemUi(
    id = id,
    persona = personaNombre,
    concepto = concepto,
    tipo = tipo,
    estado = estado,
    saldoTexto = saldo.toMoneyText(),
    totalTexto = montoTotal.toMoneyText(),
    vencimientoTexto = vencimiento?.toDateText(),
    vencida = estaVencida(hoy),
    progreso = progreso(),
)

fun Abono.toUi(zone: ZoneId = ZoneId.systemDefault()) = AbonoUi(
    id = id,
    montoTexto = monto.toMoneyText(),
    fechaTexto = fecha.toDateText(zone),
    nota = nota?.takeIf { it.isNotBlank() },
)

fun Deuda.toDetalleUi(hoy: LocalDate = LocalDate.now()) = DeudaDetalleUi(
    id = id,
    persona = personaNombre,
    concepto = concepto,
    tipo = tipo,
    estado = estado,
    totalTexto = montoTotal.toMoneyText(),
    abonadoTexto = abonado.toMoneyText(),
    saldoTexto = saldo.toMoneyText(),
    vencimientoTexto = vencimiento?.toDateText(),
    vencida = estaVencida(hoy),
    creadaTexto = creadaEn.toDateText(),
    progreso = progreso(),
    abonos = abonos.sortedByDescending { it.fecha }.map { it.toUi() },
    saldoPendiente = saldo.signum() > 0,
)
