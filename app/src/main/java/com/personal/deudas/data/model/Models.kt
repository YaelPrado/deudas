package com.personal.deudas.data.model

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

// Modelos que usa la app. No dependen de la red: el JSON de la API se convierte
// a estos modelos con los mappers (data/mapper). No hay capa "domain": el ViewModel
// trabaja directamente con estos modelos a través de los repositorios.

enum class TipoDeuda { ME_DEBEN, DEBO }

enum class EstadoDeuda { PENDIENTE, PARCIAL, PAGADA }

data class User(
    val id: String,
    val nombre: String,
    val correo: String,
    val rol: String,
)

data class Session(
    val accessToken: String,
    val refreshToken: String,
    val user: User,
)

data class Persona(
    val id: String,
    val nombre: String,
    val telefono: String?,
)

data class Abono(
    val id: String,
    val monto: BigDecimal,
    val nota: String?,
    val fecha: Instant,
)

data class Deuda(
    val id: String,
    val personaId: String,
    val personaNombre: String,
    val tipo: TipoDeuda,
    val concepto: String,
    val montoTotal: BigDecimal,
    val abonado: BigDecimal,
    val saldo: BigDecimal,
    val estado: EstadoDeuda,
    val vencimiento: LocalDate?,
    val creadaEn: Instant,
    /** Solo viene en el detalle; en la lista está vacía. */
    val abonos: List<Abono>,
)

/** Datos que el usuario captura para crear o editar una deuda. */
data class DeudaInput(
    val personaId: String,
    val tipo: TipoDeuda,
    val concepto: String,
    val monto: BigDecimal,
    val vencimiento: LocalDate?,
)
