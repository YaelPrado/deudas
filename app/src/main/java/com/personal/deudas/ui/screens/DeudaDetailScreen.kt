package com.personal.deudas.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import com.personal.deudas.ui.components.AppTextField
import com.personal.deudas.ui.components.ConfirmDialog
import com.personal.deudas.ui.components.ErrorState
import com.personal.deudas.ui.components.LoadingBox
import com.personal.deudas.ui.components.Pill
import com.personal.deudas.ui.state.AbonoDialogUi
import com.personal.deudas.ui.state.AbonoUi
import com.personal.deudas.ui.state.DeudaDetalleUi
import com.personal.deudas.ui.theme.colors
import com.personal.deudas.ui.util.label
import com.personal.deudas.ui.viewmodels.DeudaDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeudaDetailScreen(
    viewModel: DeudaDetailViewModel,
    mensajes: Flow<String>,
    onBack: () -> Unit,
    onEditar: (String) -> Unit,
    onEliminada: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var confirmarEliminar by rememberSaveable { mutableStateOf(false) }
    var confirmarLiquidar by rememberSaveable { mutableStateOf(false) }
    var abonoADeshacer by rememberSaveable { mutableStateOf<String?>(null) }

    // Si se editó la deuda en el formulario, al volver se recarga sin mostrar el indicador.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.cargar(silenciosa = true) }

    LaunchedEffect(mensajes) { mensajes.collect { snackbar.showSnackbar(it) } }
    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showSnackbar(it)
            viewModel.onMessageShown()
        }
    }
    LaunchedEffect(state.eliminada) { if (state.eliminada) onEliminada() }

    val deuda = state.deuda

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de la deuda") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
                },
                actions = {
                    if (deuda != null) {
                        IconButton(onClick = { onEditar(deuda.id) }, enabled = !state.isWorking) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = { confirmarEliminar = true }, enabled = !state.isWorking) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                deuda != null -> {
                    DetalleContenido(
                        deuda = deuda,
                        trabajando = state.isWorking,
                        onAbono = viewModel::onAbrirAbono,
                        onLiquidar = { confirmarLiquidar = true },
                        onDeshacer = { abonoADeshacer = it },
                    )
                    if (state.isWorking) LinearProgressIndicator(Modifier.fillMaxWidth().align(Alignment.TopCenter))
                }
                state.error != null -> ErrorState(state.error!!, onRetry = { viewModel.cargar() })
                else -> LoadingBox()
            }
        }
    }

    state.abonoDialog?.let { dialogo ->
        AbonoDialog(
            dialogo = dialogo,
            saldoTexto = deuda?.saldoTexto.orEmpty(),
            enviando = state.isWorking,
            onMonto = viewModel::onAbonoMontoChange,
            onNota = viewModel::onAbonoNotaChange,
            onConfirmar = viewModel::onConfirmarAbono,
            onCerrar = viewModel::onCerrarAbono,
        )
    }

    if (confirmarEliminar) {
        ConfirmDialog(
            title = "Eliminar deuda",
            text = "Se borrará la deuda con todos sus abonos. Esta acción no se puede deshacer.",
            confirmText = "Eliminar",
            destructive = true,
            onConfirm = {
                confirmarEliminar = false
                viewModel.onEliminar()
            },
            onDismiss = { confirmarEliminar = false },
        )
    }
    if (confirmarLiquidar) {
        ConfirmDialog(
            title = "Liquidar deuda",
            text = "Se registrará un abono por el saldo restante (${deuda?.saldoTexto.orEmpty()}) y la deuda quedará pagada.",
            confirmText = "Liquidar",
            onConfirm = {
                confirmarLiquidar = false
                viewModel.onLiquidar()
            },
            onDismiss = { confirmarLiquidar = false },
        )
    }
    abonoADeshacer?.let { id ->
        ConfirmDialog(
            title = "Deshacer abono",
            text = "El monto de este abono volverá a sumarse al saldo de la deuda.",
            confirmText = "Deshacer",
            destructive = true,
            onConfirm = {
                abonoADeshacer = null
                viewModel.onDeshacerAbono(id)
            },
            onDismiss = { abonoADeshacer = null },
        )
    }
}

@Composable
private fun DetalleContenido(
    deuda: DeudaDetalleUi,
    trabajando: Boolean,
    onAbono: () -> Unit,
    onLiquidar: () -> Unit,
    onDeshacer: (String) -> Unit,
) {
    val tipoColors = deuda.tipo.colors()
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Pill(deuda.tipo.label, tipoColors)
                    Pill(deuda.estado.label, deuda.estado.colors())
                }
                Text(deuda.persona, style = MaterialTheme.typography.headlineSmall)
                Text(deuda.concepto, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                HorizontalDivider()
                Text("Saldo pendiente", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(deuda.saldoTexto, style = MaterialTheme.typography.headlineMedium, color = tipoColors.content)
                LinearProgressIndicator(progress = { deuda.progreso }, modifier = Modifier.fillMaxWidth())
                DatoFila("Monto total", deuda.totalTexto)
                DatoFila("Abonado", deuda.abonadoTexto)
                DatoFila("Creada", deuda.creadaTexto)
                if (deuda.vencimientoTexto != null) {
                    DatoFila(
                        "Vencimiento",
                        if (deuda.vencida) "${deuda.vencimientoTexto} (vencida)" else deuda.vencimientoTexto,
                        destacado = deuda.vencida,
                    )
                }
            }
        }

        if (deuda.saldoPendiente) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onAbono, enabled = !trabajando, modifier = Modifier.fillMaxWidth()) { Text("Registrar abono") }
                OutlinedButton(onClick = onLiquidar, enabled = !trabajando, modifier = Modifier.fillMaxWidth()) { Text("Liquidar deuda") }
            }
        }

        Text("Abonos (${deuda.abonos.size})", style = MaterialTheme.typography.titleMedium)
        if (deuda.abonos.isEmpty()) {
            Text("Todavía no hay abonos.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                deuda.abonos.forEachIndexed { index, abono ->
                    if (index > 0) HorizontalDivider()
                    AbonoFila(abono, enabled = !trabajando, onDeshacer = { onDeshacer(abono.id) })
                }
            }
        }
    }
}

@Composable
private fun DatoFila(etiqueta: String, valor: String, destacado: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            valor,
            style = MaterialTheme.typography.bodyMedium,
            color = if (destacado) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun AbonoFila(abono: AbonoUi, enabled: Boolean, onDeshacer: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(abono.montoTexto, style = MaterialTheme.typography.titleSmall)
            Text(abono.fechaTexto, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (abono.nota != null) {
                Text(abono.nota, style = MaterialTheme.typography.bodySmall)
            }
        }
        IconButton(onClick = onDeshacer, enabled = enabled) {
            Icon(Icons.Default.Close, contentDescription = "Deshacer abono")
        }
    }
}

@Composable
private fun AbonoDialog(
    dialogo: AbonoDialogUi,
    saldoTexto: String,
    enviando: Boolean,
    onMonto: (String) -> Unit,
    onNota: (String) -> Unit,
    onConfirmar: () -> Unit,
    onCerrar: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { if (!enviando) onCerrar() },
        title = { Text("Registrar abono") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppTextField(
                    value = dialogo.monto,
                    onValueChange = onMonto,
                    label = "Monto",
                    helper = "Saldo pendiente: $saldoTexto",
                    error = dialogo.montoError,
                    keyboardType = KeyboardType.Decimal,
                    enabled = !enviando,
                    modifier = Modifier.fillMaxWidth(),
                )
                AppTextField(
                    value = dialogo.nota,
                    onValueChange = onNota,
                    label = "Nota (opcional)",
                    error = dialogo.notaError,
                    imeAction = ImeAction.Done,
                    onImeAction = onConfirmar,
                    enabled = !enviando,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = { TextButton(onClick = onConfirmar, enabled = !enviando) { Text("Registrar") } },
        dismissButton = { TextButton(onClick = onCerrar, enabled = !enviando) { Text("Cancelar") } },
    )
}
