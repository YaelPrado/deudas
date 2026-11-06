package com.personal.deudas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.personal.deudas.data.model.TipoDeuda
import com.personal.deudas.ui.components.AppTextField
import com.personal.deudas.ui.components.DatePickerField
import com.personal.deudas.ui.components.ErrorState
import com.personal.deudas.ui.components.LoadingBox
import com.personal.deudas.ui.components.PersonaSelector
import com.personal.deudas.ui.state.NuevaPersonaUi
import com.personal.deudas.ui.util.label
import com.personal.deudas.ui.viewmodels.DeudaFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeudaFormScreen(
    viewModel: DeudaFormViewModel,
    onBack: () -> Unit,
    onGuardada: (mensaje: String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(state.guardada) {
        if (state.guardada) onGuardada(if (state.esEdicion) "Deuda actualizada." else "Deuda creada.")
    }
    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showSnackbar(it)
            viewModel.onMessageShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.esEdicion) "Editar deuda" else "Nueva deuda") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        when {
            state.isLoading -> LoadingBox(Modifier.padding(padding))
            state.loadError != null -> ErrorState(state.loadError!!, onRetry = viewModel::cargar, modifier = Modifier.padding(padding))
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("Tipo de deuda", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TipoDeuda.entries.forEach { tipo ->
                        FilterChip(
                            selected = state.tipo == tipo,
                            onClick = { viewModel.onTipoChange(tipo) },
                            label = { Text(tipo.label) },
                            enabled = !state.isSaving,
                        )
                    }
                }
                PersonaSelector(
                    personas = state.personas,
                    selectedName = state.personaNombre,
                    onSelect = { viewModel.onPersonaChange(it.id) },
                    onNueva = viewModel::onAbrirNuevaPersona,
                    error = state.personaError,
                )
                AppTextField(
                    value = state.concepto,
                    onValueChange = viewModel::onConceptoChange,
                    label = "Concepto",
                    error = state.conceptoError,
                    capitalization = KeyboardCapitalization.Sentences,
                    enabled = !state.isSaving,
                    modifier = Modifier.fillMaxWidth(),
                )
                AppTextField(
                    value = state.monto,
                    onValueChange = viewModel::onMontoChange,
                    label = "Monto (MXN)",
                    error = state.montoError,
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done,
                    enabled = !state.isSaving,
                    modifier = Modifier.fillMaxWidth(),
                )
                DatePickerField(
                    label = "Fecha de vencimiento (opcional)",
                    value = state.vencimiento,
                    onChange = viewModel::onVencimientoChange,
                )
                Button(
                    onClick = viewModel::onGuardar,
                    enabled = !state.isSaving,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Guardar")
                    }
                }
            }
        }
    }

    state.nuevaPersona?.let { np ->
        NuevaPersonaDialog(
            np = np,
            onNombre = viewModel::onNuevaPersonaNombre,
            onTelefono = viewModel::onNuevaPersonaTelefono,
            onGuardar = viewModel::onGuardarNuevaPersona,
            onCerrar = viewModel::onCerrarNuevaPersona,
        )
    }
}

@Composable
private fun NuevaPersonaDialog(
    np: NuevaPersonaUi,
    onNombre: (String) -> Unit,
    onTelefono: (String) -> Unit,
    onGuardar: () -> Unit,
    onCerrar: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { if (!np.guardando) onCerrar() },
        title = { Text("Nueva persona") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                np.error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                AppTextField(
                    value = np.nombre,
                    onValueChange = onNombre,
                    label = "Nombre",
                    error = np.nombreError,
                    capitalization = KeyboardCapitalization.Words,
                    enabled = !np.guardando,
                    modifier = Modifier.fillMaxWidth(),
                )
                AppTextField(
                    value = np.telefono,
                    onValueChange = onTelefono,
                    label = "Teléfono (opcional)",
                    error = np.telefonoError,
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done,
                    onImeAction = onGuardar,
                    enabled = !np.guardando,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = { TextButton(onClick = onGuardar, enabled = !np.guardando) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onCerrar, enabled = !np.guardando) { Text("Cancelar") } },
    )
}
