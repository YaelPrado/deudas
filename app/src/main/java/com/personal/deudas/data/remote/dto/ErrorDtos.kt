package com.personal.deudas.data.remote.dto

import kotlinx.serialization.Serializable

// Formato único de error de la API: { "error": { "codigo": "...", "mensaje": "..." } }

@Serializable
data class ErrorDetalleDto(
    val codigo: String,
    val mensaje: String,
)

@Serializable
data class ErrorResponseDto(
    val error: ErrorDetalleDto,
)
