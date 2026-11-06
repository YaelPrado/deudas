package com.personal.deudas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.personal.deudas.data.repository.AuthRepository
import com.personal.deudas.ui.state.RegisterUiState
import com.personal.deudas.ui.util.Validators
import com.personal.deudas.ui.util.userMessage

class RegisterViewModel(private val auth: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    fun onNombreChange(valor: String) =
        _state.update { it.copy(nombre = valor, nombreError = null, error = null) }

    fun onCorreoChange(valor: String) =
        _state.update { it.copy(correo = valor, correoError = null, error = null) }

    fun onContrasenaChange(valor: String) =
        _state.update { it.copy(contrasena = valor, contrasenaError = null, confirmacionError = null, error = null) }

    fun onConfirmacionChange(valor: String) =
        _state.update { it.copy(confirmacion = valor, confirmacionError = null, error = null) }

    fun onSubmit() {
        val actual = _state.value
        if (actual.isLoading) return
        val nombreError = Validators.nombre(actual.nombre)
        val correoError = Validators.correo(actual.correo)
        val contrasenaError = Validators.contrasenaNueva(actual.contrasena)
        val confirmacionError = Validators.confirmacion(actual.contrasena, actual.confirmacion)
        if (listOf(nombreError, correoError, contrasenaError, confirmacionError).any { it != null }) {
            _state.update {
                it.copy(
                    nombreError = nombreError,
                    correoError = correoError,
                    contrasenaError = contrasenaError,
                    confirmacionError = confirmacionError,
                )
            }
            return
        }
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            auth.register(actual.nombre, actual.correo, actual.contrasena)
                .onFailure { e -> _state.update { it.copy(error = e.userMessage()) } }
            _state.update { it.copy(isLoading = false) }
        }
    }
}
