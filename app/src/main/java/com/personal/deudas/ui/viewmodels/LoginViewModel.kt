package com.personal.deudas.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.personal.deudas.data.repository.AuthRepository
import com.personal.deudas.ui.state.LoginUiState
import com.personal.deudas.ui.util.Validators
import com.personal.deudas.ui.util.userMessage

class LoginViewModel(private val auth: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onCorreoChange(valor: String) =
        _state.update { it.copy(correo = valor, correoError = null, error = null) }

    fun onContrasenaChange(valor: String) =
        _state.update { it.copy(contrasena = valor, contrasenaError = null, error = null) }

    fun onSubmit() {
        val actual = _state.value
        if (actual.isLoading) return
        val correoError = Validators.correo(actual.correo)
        val contrasenaError = Validators.contrasenaLogin(actual.contrasena)
        if (correoError != null || contrasenaError != null) {
            _state.update { it.copy(correoError = correoError, contrasenaError = contrasenaError) }
            return
        }
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            // Si sale bien, la sesión queda guardada y la navegación cambia sola a Home.
            auth.login(actual.correo, actual.contrasena)
                .onFailure { e -> _state.update { it.copy(error = e.userMessage()) } }
            _state.update { it.copy(isLoading = false) }
        }
    }
}
