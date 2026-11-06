package com.personal.deudas.data.mapper

import java.time.Instant
import java.time.LocalDate
import com.personal.deudas.data.model.Abono
import com.personal.deudas.data.model.Deuda
import com.personal.deudas.data.model.DeudaInput
import com.personal.deudas.data.model.EstadoDeuda
import com.personal.deudas.data.model.TipoDeuda
import com.personal.deudas.data.remote.dto.AbonoDto
import com.personal.deudas.data.remote.dto.AbonoRequestDto
import com.personal.deudas.data.remote.dto.DeudaDto
import com.personal.deudas.data.remote.dto.DeudaRequestDto
import java.math.BigDecimal

// DTO -> modelo: centavos a pesos, texto a enums y texto ISO a fechas.

fun AbonoDto.toModel(): Abono = Abono(
    id = id,
    monto = montoCentavos.centavosToPesos(),
    nota = nota,
    fecha = Instant.parse(fecha),
)

fun DeudaDto.toModel(): Deuda = Deuda(
    id = id,
    personaId = personaId,
    personaNombre = personaNombre,
    tipo = TipoDeuda.valueOf(tipo),
    concepto = concepto,
    montoTotal = montoTotalCentavos.centavosToPesos(),
    abonado = totalAbonadoCentavos.centavosToPesos(),
    saldo = saldoCentavos.centavosToPesos(),
    estado = EstadoDeuda.valueOf(estado),
    vencimiento = vencimiento?.let { LocalDate.parse(it) },
    creadaEn = Instant.parse(creadaEn),
    abonos = abonos.map { it.toModel() },
)

// Modelo -> DTO de petición: pesos a centavos y fechas a texto.

fun DeudaInput.toRequest(): DeudaRequestDto = DeudaRequestDto(
    personaId = personaId,
    tipo = tipo.name,
    concepto = concepto.trim(),
    montoTotalCentavos = monto.pesosToCentavos(),
    vencimiento = vencimiento?.toString(),
)

fun abonoRequest(monto: BigDecimal, nota: String?): AbonoRequestDto = AbonoRequestDto(
    montoCentavos = monto.pesosToCentavos(),
    nota = nota?.trim()?.takeIf { it.isNotEmpty() },
)
