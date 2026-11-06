package com.personal.deudas.data.repository

import java.math.BigDecimal
import kotlinx.serialization.json.Json
import com.personal.deudas.data.mapper.abonoRequest
import com.personal.deudas.data.mapper.toModel
import com.personal.deudas.data.mapper.toRequest
import com.personal.deudas.data.model.Deuda
import com.personal.deudas.data.model.DeudaInput
import com.personal.deudas.data.remote.api.DeudasApi

class DeudaRepositoryImpl(
    private val api: DeudasApi,
    private val json: Json,
) : DeudaRepository {

    override suspend fun listar(): Result<List<Deuda>> =
        safeApiCall(json) { api.deudas().map { it.toModel() } }

    override suspend fun obtener(id: String): Result<Deuda> =
        safeApiCall(json) { api.deuda(id).toModel() }

    override suspend fun crear(input: DeudaInput): Result<Deuda> =
        safeApiCall(json) { api.crearDeuda(input.toRequest()).toModel() }

    override suspend fun editar(id: String, input: DeudaInput): Result<Deuda> =
        safeApiCall(json) { api.editarDeuda(id, input.toRequest()).toModel() }

    override suspend fun eliminar(id: String): Result<Unit> =
        safeApiCall(json) { api.eliminarDeuda(id) }

    override suspend fun registrarAbono(id: String, monto: BigDecimal, nota: String?): Result<Deuda> =
        safeApiCall(json) { api.registrarAbono(id, abonoRequest(monto, nota)).toModel() }

    override suspend fun deshacerAbono(id: String, abonoId: String): Result<Deuda> =
        safeApiCall(json) { api.deshacerAbono(id, abonoId).toModel() }

    override suspend fun liquidar(id: String): Result<Deuda> =
        safeApiCall(json) { api.liquidar(id).toModel() }
}
