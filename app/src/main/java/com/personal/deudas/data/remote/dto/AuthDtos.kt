package com.personal.deudas.data.remote.dto

import kotlinx.serialization.Serializable

// DTO: reflejan el JSON de la API tal cual viaja. Nunca llegan a los ViewModels.

@Serializable
data class LoginRequestDto(
    val correo: String,
    val contrasena: String,
)

@Serializable
data class RegistroRequestDto(
    val nombre: String,
    val correo: String,
    val contrasena: String,
)

@Serializable
data class RefrescarRequestDto(
    val refreshToken: String,
)

@Serializable
data class UsuarioDto(
    val id: String,
    val nombre: String,
    val correo: String,
    val rol: String = "USUARIO",
)

@Serializable
data class SesionDto(
    val token: String,
    val refreshToken: String,
    val usuario: UsuarioDto,
)
