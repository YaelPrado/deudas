package com.personal.deudas.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.personal.deudas.di.AppContainer
import com.personal.deudas.ui.viewmodels.DeudaDetailViewModel
import com.personal.deudas.ui.viewmodels.DeudaFormViewModel
import com.personal.deudas.ui.viewmodels.HomeViewModel
import com.personal.deudas.ui.viewmodels.LoginViewModel
import com.personal.deudas.ui.viewmodels.MainViewModel
import com.personal.deudas.ui.viewmodels.RegisterViewModel

/**
 * Única fábrica de ViewModels. Toma las dependencias del [AppContainer] (inyección manual).
 * Los ViewModels que reciben argumentos de navegación obtienen el SavedStateHandle de [extras].
 */
class ViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val viewModel: ViewModel = when {
            modelClass.isAssignableFrom(MainViewModel::class.java) ->
                MainViewModel(container.authRepository)

            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(container.authRepository)

            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->
                RegisterViewModel(container.authRepository)

            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(container.deudaRepository, container.authRepository)

            modelClass.isAssignableFrom(DeudaDetailViewModel::class.java) ->
                DeudaDetailViewModel(extras.createSavedStateHandle(), container.deudaRepository)

            modelClass.isAssignableFrom(DeudaFormViewModel::class.java) ->
                DeudaFormViewModel(extras.createSavedStateHandle(), container.deudaRepository, container.personaRepository)

            else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
        return viewModel as T
    }
}
