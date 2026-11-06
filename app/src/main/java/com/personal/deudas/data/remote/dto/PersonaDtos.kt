package com.personal.deudas.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PersonaDto(
    val id: String,
    val nombre: String,
    val telefono: String? = null,
)

@Serializable
data class PersonaRequestDto(
    val nombre: String,
    val telefono: String? = null,
)
