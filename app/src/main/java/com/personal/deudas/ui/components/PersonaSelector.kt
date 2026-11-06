package com.personal.deudas.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.personal.deudas.data.model.Persona

/** Lista desplegable para elegir la persona de la deuda, con la opción de crear una nueva. */
@Composable
fun PersonaSelector(
    personas: List<Persona>,
    selectedName: String?,
    onSelect: (Persona) -> Unit,
    onNueva: () -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
) {
    var abierto by rememberSaveable { mutableStateOf(false) }

    Box(modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedName ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Persona") },
            placeholder = { Text("Elige una persona") },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
            isError = error != null,
            supportingText = error?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
        )
        Box(Modifier.matchParentSize().clickable { abierto = true })
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            personas.forEach { persona ->
                DropdownMenuItem(
                    text = { Text(persona.nombre) },
                    onClick = {
                        abierto = false
                        onSelect(persona)
                    },
                )
            }
            if (personas.isNotEmpty()) HorizontalDivider()
            DropdownMenuItem(
                text = { Text("Nueva persona") },
                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
                onClick = {
                    abierto = false
                    onNueva()
                },
            )
        }
    }
}