package com.personal.deudas.data.mapper

import com.personal.deudas.data.model.Session
import com.personal.deudas.data.model.User
import com.personal.deudas.data.remote.dto.SesionDto
import com.personal.deudas.data.remote.dto.UsuarioDto

fun UsuarioDto.toModel(): User = User(id = id, nombre = nombre, correo = correo, rol = rol)

fun SesionDto.toModel(): Session = Session(
    accessToken = token,
    refreshToken = refreshToken,
    user = usuario.toModel(),
)
