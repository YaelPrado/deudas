package com.personal.deudas.data.repository

import com.personal.deudas.data.model.Persona

/** Las personas con las que se tienen deudas. Se usan al crear o editar una deuda. */
interface PersonaRepository {
    suspend fun listar(): Result<List<Persona>>

    suspend fun crear(nombre: String, telefono: String?): Result<Persona>
}
