package com.rhapp.presentation.ui.admin.nominas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rhapp.domain.model.EstadoNomina
import com.rhapp.theme.*

fun nominaEstadoColor(estado: EstadoNomina) = when (estado) {
    EstadoNomina.GENERADA -> NominaGenerada
    EstadoNomina.REVISADA -> NominaRevisada
    EstadoNomina.PAGADA   -> NominaPagada
    EstadoNomina.ANULADA  -> NominaAnulada
}

@Composable
fun EstadoNominaDropdown(
    current:    EstadoNomina,
    onPagada:   () -> Unit,    // Sólo se puede marcar como pagada desde la API
    modifier:   Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val color    = nominaEstadoColor(current)

    Box(modifier = modifier) {
        Surface(
            onClick = { if (current != EstadoNomina.PAGADA && current != EstadoNomina.ANULADA) expanded = true },
            shape   = CircleShape,
            color   = color.copy(alpha = 0.12f),
            border  = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        ) {
            Row(
                modifier              = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Box(modifier = Modifier.size(7.dp).background(color, CircleShape))
                Text(text = current.label, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                if (current != EstadoNomina.PAGADA && current != EstadoNomina.ANULADA) {
                    Icon(Icons.Default.ArrowDropDown, null, tint = color, modifier = Modifier.size(14.dp))
                }
            }
        }

        // Solo se puede marcar como "Pagada" desde el dropdown (endpoint dedicado)
        DropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false },
            modifier         = Modifier.background(Surface),
        ) {
            EstadoNomina.entries.forEach { estado ->
                val eColor     = nominaEstadoColor(estado)
                val isSelected = estado == current
                // Solo mostrar Pagada como acción posible si estamos en Revisada
                val isEnabled  = estado == EstadoNomina.PAGADA && current == EstadoNomina.REVISADA

                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.size(8.dp).background(eColor, CircleShape))
                            Text(
                                text       = estado.label,
                                color      = if (isEnabled || isSelected) eColor else TextFaint,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize   = 13.sp,
                            )
                        }
                    },
                    trailingIcon = if (isSelected) {
                        { Icon(Icons.Default.Check, null, tint = eColor, modifier = Modifier.size(14.dp)) }
                    } else null,
                    onClick = {
                        expanded = false
                        if (isEnabled) onPagada()
                    },
                    enabled = isEnabled || isSelected,
                    colors  = MenuDefaults.itemColors(textColor = if (isEnabled) eColor else TextFaint),
                )
            }
        }
    }
}
