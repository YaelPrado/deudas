package com.personal.deudas.data.repository

import kotlinx.serialization.json.Json
import com.personal.deudas.data.mapper.personaRequest
import com.personal.deudas.data.mapper.toModel
import com.personal.deudas.data.model.Persona
import com.personal.deudas.data.remote.api.DeudasApi

class PersonaRepositoryImpl(
    private val api: DeudasApi,
    private val json: Json,
) : PersonaRepository {

    override suspend fun listar(): Result<List<Persona>> =
        safeApiCall(json) { api.personas().map { it.toModel() } }

    override suspend fun crear(nombre: String, telefono: String?): Result<Persona> =
        safeApiCall(json) { api.crearPersona(personaRequest(nombre, telefono)).toModel() }
}
