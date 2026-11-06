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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.personal.deudas.ui.components.AppTextField
import com.personal.deudas.ui.components.ErrorBanner
import com.personal.deudas.ui.viewmodels.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(viewModel: RegisterViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta") },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !state.isLoading) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Con tu cuenta tus deudas quedan guardadas y las ves desde cualquier dispositivo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            state.error?.let { ErrorBanner(it) }

            AppTextField(
                value = state.nombre,
                onValueChange = viewModel::onNombreChange,
                label = "Nombre",
                error = state.nombreError,
                capitalization = KeyboardCapitalization.Words,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )
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
                helper = "Mínimo 8 caracteres.",
                error = state.contrasenaError,
                isPassword = true,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )
            AppTextField(
                value = state.confirmacion,
                onValueChange = viewModel::onConfirmacionChange,
                label = "Confirmar contraseña",
                error = state.confirmacionError,
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
                    Text("Crear cuenta")
                }
            }
        }
    }
}
