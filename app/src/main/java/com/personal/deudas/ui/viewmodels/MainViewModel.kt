package com.personal.deudas.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import com.personal.deudas.data.repository.AuthRepository

/** Solo expone si hay sesión: la navegación raíz reacciona a ella. */
class MainViewModel(authRepository: AuthRepository) : ViewModel() {
    val hasSession: StateFlow<Boolean> = authRepository.hasSession
}
