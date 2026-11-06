package com.personal.deudas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.personal.deudas.ui.components.AppTextField
import com.personal.deudas.ui.components.ErrorBanner
import com.personal.deudas.ui.viewmodels.LoginViewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel, onGoRegister: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        ) {
            Text("Deudas", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
            Text(
                "Inicia sesión para ver quién te debe y a quién le debes.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            state.error?.let { ErrorBanner(it) }

            AppTextField(
                value = state.correo,
                onValueChange = viewModel::onCorreoChange,
                label = "Correo",
                error = state.correoError,
                keyboardType = KeyboardType.Email,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )
            AppTextField(
                value = state.contrasena,
                onValueChange = viewModel::onContrasenaChange,
                label = "Contraseña",
                error = state.contrasenaError,
                isPassword = true,
                imeAction = ImeAction.Done,
                onImeAction = viewModel::onSubmit,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = viewModel::onSubmit,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Iniciar sesión")
                }
            }
            TextButton(onClick = onGoRegister, enabled = !state.isLoading, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}
