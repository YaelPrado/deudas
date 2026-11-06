package com.personal.deudas.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import com.personal.deudas.factory.ViewModelFactory
import com.personal.deudas.ui.screens.DeudaDetailScreen
import com.personal.deudas.ui.screens.DeudaFormScreen
import com.personal.deudas.ui.screens.HomeScreen
import com.personal.deudas.ui.screens.LoginScreen
import com.personal.deudas.ui.screens.RegisterScreen
import com.personal.deudas.ui.viewmodels.DeudaDetailViewModel
import com.personal.deudas.ui.viewmodels.DeudaFormViewModel
import com.personal.deudas.ui.viewmodels.HomeViewModel
import com.personal.deudas.ui.viewmodels.LoginViewModel
import com.personal.deudas.ui.viewmodels.MainViewModel
import com.personal.deudas.ui.viewmodels.RegisterViewModel

@Composable
fun AppNavHost(factory: ViewModelFactory, mainViewModel: MainViewModel, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val hasSession by mainViewModel.hasSession.collectAsStateWithLifecycle()

    // La sesión manda: si se pierde (logout o sesión inválida) se vuelve al login; si se inicia, se entra a Home.
    LaunchedEffect(hasSession) {
        val destino = if (hasSession) Routes.HOME else Routes.LOGIN
        val actual = navController.currentDestination?.route
        if (actual != null && actual != destino && esPantallaDeSesion(actual) != hasSession) {
            navController.navigate(destino) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (hasSession) Routes.HOME else Routes.LOGIN,
        modifier = modifier,
    ) {
        composable(Routes.LOGIN) {
            val vm: LoginViewModel = viewModel(factory = factory)
            LoginScreen(vm, onGoRegister = { navController.navigate(Routes.REGISTER) })
        }
        composable(Routes.REGISTER) {
            val vm: RegisterViewModel = viewModel(factory = factory)
            RegisterScreen(vm, onBack = { navController.popBackStack() })
        }
        composable(Routes.HOME) { entry ->
            val vm: HomeViewModel = viewModel(factory = factory)
            val mensajes = remember(entry) { entry.mensajes() }
            HomeScreen(
                viewModel = vm,
                mensajes = mensajes,
                onOpenDeuda = { navController.navigate(Routes.detail(it)) },
                onNuevaDeuda = { navController.navigate(Routes.formNueva()) },
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument(Routes.ARG_DEUDA_ID) { type = NavType.StringType }),
        ) { entry ->
            val vm: DeudaDetailViewModel = viewModel(factory = factory)
            val mensajes = remember(entry) { entry.mensajes() }
            DeudaDetailScreen(
                viewModel = vm,
                mensajes = mensajes,
                onBack = { navController.popBackStack() },
                onEditar = { navController.navigate(Routes.formEditar(it)) },
                onEliminada = { navController.volverConMensaje("Deuda eliminada.") },
            )
        }
        composable(
            route = Routes.FORM,
            arguments = listOf(
                navArgument(Routes.ARG_DEUDA_ID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
        ) {
            val vm: DeudaFormViewModel = viewModel(factory = factory)
            DeudaFormScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onGuardada = { navController.volverConMensaje(it) },
            )
        }
    }
}

private fun esPantallaDeSesion(route: String): Boolean = route != Routes.LOGIN && route != Routes.REGISTER

/** Mensajes de una sola vez que otra pantalla dejó para esta (se limpian al leerlos). */
private fun NavBackStackEntry.mensajes(): Flow<String> =
    savedStateHandle.getStateFlow<String?>(Routes.KEY_MENSAJE, null)
        .filterNotNull()
        .onEach { savedStateHandle[Routes.KEY_MENSAJE] = null }

/** Regresa a la pantalla anterior dejándole un mensaje para mostrar en su snackbar. */
private fun NavHostController.volverConMensaje(mensaje: String) {
    previousBackStackEntry?.savedStateHandle?.set(Routes.KEY_MENSAJE, mensaje)
    popBackStack()
}
