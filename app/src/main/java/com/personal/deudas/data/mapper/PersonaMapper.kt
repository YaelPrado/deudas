package com.personal.deudas.data.mapper

import com.personal.deudas.data.model.Persona
import com.personal.deudas.data.remote.dto.PersonaDto
import com.personal.deudas.data.remote.dto.PersonaRequestDto

fun PersonaDto.toModel(): Persona = Persona(id = id, nombre = nombre, telefono = telefono)

/** El teléfono vacío se envía como "no hay teléfono". */
fun personaRequest(nombre: String, telefono: String?): PersonaRequestDto = PersonaRequestDto(
    nombre = nombre.trim(),
    telefono = telefono?.trim()?.takeIf { it.isNotEmpty() },
)
