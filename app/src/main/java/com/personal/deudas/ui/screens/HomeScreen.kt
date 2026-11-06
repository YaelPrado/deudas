package com.personal.deudas.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import com.personal.deudas.data.model.EstadoDeuda
import com.personal.deudas.data.model.TipoDeuda
import com.personal.deudas.ui.components.ConfirmDialog
import com.personal.deudas.ui.components.DeudaCard
import com.personal.deudas.ui.components.EmptyState
import com.personal.deudas.ui.components.ErrorState
import com.personal.deudas.ui.components.LoadingBox
import com.personal.deudas.ui.theme.colors
import com.personal.deudas.ui.util.label
import com.personal.deudas.ui.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    mensajes: Flow<String>,
    onOpenDeuda: (String) -> Unit,
    onNuevaDeuda: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var confirmarLogout by rememberSaveable { mutableStateOf(false) }

    // Al volver a esta pantalla (desde el detalle o el formulario) se refresca la lista sin parpadeo.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.cargar(silenciosa = true) }

    LaunchedEffect(mensajes) { mensajes.collect { snackbar.showSnackbar(it) } }
    LaunchedEffect(state.message) {
        state.message?.let {
            snackbar.showSnackbar(it)
            viewModel.onMessageShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis deudas") },
                actions = {
                    IconButton(onClick = { viewModel.cargar() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                    IconButton(onClick = { confirmarLogout = true }, enabled = !state.isLoggingOut) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevaDeuda,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Nueva deuda") },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading && state.totalDeudas == 0 -> LoadingBox()
                state.error != null -> ErrorState(state.error!!, onRetry = { viewModel.cargar() })
                state.totalDeudas == 0 -> EmptyState(
                    icon = Icons.Default.AccountBalanceWallet,
                    title = "Aún no tienes deudas",
                    message = "Registra lo que te deben o lo que debes para llevar el control.",
                    actionLabel = "Nueva deuda",
                    onAction = onNuevaDeuda,
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item(key = "resumen") {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            ResumenCard("Me deben", state.meDebenTexto, TipoDeuda.ME_DEBEN, Modifier.weight(1f))
                            ResumenCard("Debo", state.deboTexto, TipoDeuda.DEBO, Modifier.weight(1f))
                        }
                    }
                    item(key = "filtros") {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(state.tipoFiltro == null, { viewModel.onTipoFiltro(null) }, label = { Text("Todas") })
                                TipoDeuda.entries.forEach { tipo ->
                                    FilterChip(state.tipoFiltro == tipo, { viewModel.onTipoFiltro(tipo) }, label = { Text(tipo.label) })
                                }
                            }
                            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(state.estadoFiltro == null, { viewModel.onEstadoFiltro(null) }, label = { Text("Todos") })
                                EstadoDeuda.entries.forEach { estado ->
                                    FilterChip(state.estadoFiltro == estado, { viewModel.onEstadoFiltro(estado) }, label = { Text(estado.label) })
                                }
                            }
                        }
                    }
                    if (state.items.isEmpty()) {
                        item(key = "sin-resultados") {
                            Text(
                                "No hay deudas con estos filtros.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            )
                        }
                    } else {
                        items(state.items, key = { it.id }) { item ->
                            DeudaCard(item = item, onClick = { onOpenDeuda(item.id) })
                        }
                    }
                }
            }
        }
    }

    if (confirmarLogout) {
        ConfirmDialog(
            title = "Cerrar sesión",
            text = "Tendrás que iniciar sesión de nuevo para ver tus deudas.",
            confirmText = "Cerrar sesión",
            onConfirm = {
                confirmarLogout = false
                viewModel.onLogout()
            },
            onDismiss = { confirmarLogout = false },
        )
    }
}

@Composable
private fun ResumenCard(titulo: String, monto: String, tipo: TipoDeuda, modifier: Modifier = Modifier) {
    val c = tipo.colors()
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = c.container),
        border = BorderStroke(0.dp, c.container),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(titulo, style = MaterialTheme.typography.labelLarge, color = c.content)
            Text(monto, style = MaterialTheme.typography.titleLarge, color = c.content)
            Text("por saldar", style = MaterialTheme.typography.bodySmall, color = c.content)
        }
    }
}
