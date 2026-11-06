package com.personal.deudas.data.remote.dto

import kotlinx.serialization.Serializable

// El dinero viaja en centavos enteros (150000 = $1,500.00) y las fechas como texto ISO 8601.

@Serializable
data class AbonoDto(
    val id: String,
    val montoCentavos: Long,
    val nota: String? = null,
    val fecha: String,
)

@Serializable
data class DeudaDto(
    val id: String,
    val personaId: String,
    val personaNombre: String,
    val tipo: String,
    val concepto: String,
    val montoTotalCentavos: Long,
    val totalAbonadoCentavos: Long,
    val saldoCentavos: Long,
    val estado: String,
    /** AAAA-MM-DD o null. */
    val vencimiento: String? = null,
    val creadaEn: String,
    /** Solo viene en el detalle y al registrar o deshacer abonos. */
    val abonos: List<AbonoDto> = emptyList(),
)

@Serializable
data class DeudaRequestDto(
    val personaId: String,
    val tipo: String,
    val concepto: String,
    val montoTotalCentavos: Long,
    val vencimiento: String? = null,
)

@Serializable
data class AbonoRequestDto(
    val montoCentavos: Long,
    val nota: String? = null,
)
