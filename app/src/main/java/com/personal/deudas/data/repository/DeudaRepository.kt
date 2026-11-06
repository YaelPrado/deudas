package com.personal.deudas.data.repository

import java.math.BigDecimal
import com.personal.deudas.data.model.Deuda
import com.personal.deudas.data.model.DeudaInput

/** CRUD de deudas y sus abonos. Expone modelos de la app, nunca DTO ni detalles de red. */
interface DeudaRepository {
    suspend fun listar(): Result<List<Deuda>>

    suspend fun obtener(id: String): Result<Deuda>

    suspend fun crear(input: DeudaInput): Result<Deuda>

    suspend fun editar(id: String, input: DeudaInput): Result<Deuda>

    suspend fun eliminar(id: String): Result<Unit>

    suspend fun registrarAbono(id: String, monto: BigDecimal, nota: String?): Result<Deuda>

    suspend fun deshacerAbono(id: String, abonoId: String): Result<Deuda>

    suspend fun liquidar(id: String): Result<Deuda>
}
