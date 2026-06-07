package com.rhapp.presentation.ui.admin.puestos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rhapp.domain.model.Puesto
import com.rhapp.presentation.components.LoadingScreen
import com.rhapp.presentation.viewmodel.PuestosAdminViewModel
import com.rhapp.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuestosAdminScreen(
    viewModel: PuestosAdminViewModel = hiltViewModel(),
) {
    val state     by viewModel.state.collectAsState()
    val filtered  by viewModel.filtered.collectAsState()
    val formState by viewModel.formState.collectAsState()

    var showForm     by remember { mutableStateOf(false) }
    var editTarget   by remember { mutableStateOf<Puesto?>(null) }
    var deleteTarget by remember { mutableStateOf<Puesto?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        // ── Header ─────────────────────────────────────────────
        Surface(color = Surface, tonalElevation = 0.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text       = "Puestos",
                            style      = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color      = TextPrimary,
                        )
                        Text(
                            text  = "${state.puestos.size} puestos",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    }
                    Button(
                        onClick = { editTarget = null; showForm = true },
                        colors  = ButtonDefaults.buttonColors(
                            containerColor = Accent,
                            contentColor   = AccentOnDark,
                        ),
                        shape          = MaterialTheme.shapes.medium,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Nuevo", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Búsqueda
                OutlinedTextField(
                    value         = state.search,
                    onValueChange = viewModel::setSearch,
                    placeholder   = { Text("Buscar por título, código o departamento...", color = TextFaint) },
                    leadingIcon   = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = MaterialTheme.shapes.medium,
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Accent,
                        unfocusedBorderColor = Border,
                        cursorColor          = Accent,
                    ),
                )
            }
        }

        // ── Contenido ───────────────────────────────────────────
        when {
            state.isLoading -> LoadingScreen("Cargando puestos...")
            state.error != null -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⚠️", fontSize = 40.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text  = state.error!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Error,
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = viewModel::load) { Text("Reintentar") }
                    }
                }
            }
            filtered.isEmpty() -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("💼", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            if (state.search.isBlank()) "Sin puestos" else "Sin resultados",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color      = TextPrimary,
                        )
                        if (state.search.isBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Crea el primer puesto",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                            )
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier            = Modifier.fillMaxSize(),
                    contentPadding      = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(filtered, key = { it.id }) { puesto ->
                        PuestoAdminCard(
                            puesto   = puesto,
                            onToggle = { viewModel.toggleActive(puesto.id, !puesto.activo) },
                            onEdit   = { editTarget = puesto; showForm = true },
                            onDelete = { deleteTarget = puesto },
                        )
                    }
                }
            }
        }
    }

    // ── Bottom Sheet formulario ─────────────────────────────────
    if (showForm) {
        PuestoFormSheet(
            initial       = editTarget,
            formState     = formState,
            departamentos = state.departamentos,
            onSave        = { payload ->
                if (editTarget != null) viewModel.updatePuesto(editTarget!!.id, payload)
                else viewModel.createPuesto(payload)
            },
            onDismiss = {
                showForm   = false
                editTarget = null
                viewModel.resetFormState()
            },
        )
    }

    // ── Diálogo de eliminación inteligente ──────────────────────
    deleteTarget?.let { puesto ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title            = {
                Text("¿Eliminar puesto?", color = TextPrimary)
            },
            text             = {
                Text(
                    "\"${puesto.titulo}\" se eliminará permanentemente. " +
                    "Si tiene empleados asignados el servidor rechazará la operación.",
                    color = TextSecondary,
                )
            },
            confirmButton    = {
                TextButton(onClick = {
                    viewModel.deletePuesto(puesto.id)
                    deleteTarget = null
                }) {
                    Text("Eliminar", color = Error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton    = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor   = Surface,
            shape            = MaterialTheme.shapes.large,
        )
    }
}

// ── PuestoAdminCard ────────────────────────────────────────────

@Composable
private fun PuestoAdminCard(
    puesto:   Puesto,
    onToggle: () -> Unit,
    onEdit:   () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        shape    = MaterialTheme.shapes.large,
        color    = Surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Toggle activo
            Switch(
                checked         = puesto.activo,
                onCheckedChange = { onToggle() },
                colors          = SwitchDefaults.colors(
                    checkedThumbColor    = AccentOnDark,
                    checkedTrackColor    = Accent,
                    uncheckedTrackColor  = Surface2,
                    uncheckedBorderColor = Border,
                ),
            )

            Spacer(Modifier.width(12.dp))

            // Info del puesto
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text       = puesto.titulo,
                        style      = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextPrimary,
                    )
                    if (!puesto.activo) {
                        Surface(
                            color = Error.copy(alpha = 0.12f),
                            shape = MaterialTheme.shapes.extraSmall,
                        ) {
                            Text(
                                "Inactivo",
                                color      = Error,
                                fontSize   = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier   = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                        }
                    }
                }
                Text(
                    text  = "${puesto.codigo} · ${puesto.departamentoNombre}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextFaint,
                )
                // Rango salarial
                Text(
                    text       = "S/ ${"%,.0f".format(puesto.salarioBase)} – ${"%,.0f".format(puesto.salarioMaximo)}",
                    style      = MaterialTheme.typography.bodySmall,
                    color      = Accent,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            // Acciones
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint               = TextSecondary,
                        modifier           = Modifier.size(20.dp),
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint               = Error,
                        modifier           = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}
