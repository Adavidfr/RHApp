package com.rhapp.presentation.ui.admin.empleados

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.rhapp.domain.model.Empleado
import com.rhapp.domain.model.EstadoEmpleado
import com.rhapp.presentation.components.LoadingScreen
import com.rhapp.presentation.viewmodel.EmpleadoContratoFilter
import com.rhapp.presentation.viewmodel.EmpleadoEstadoFilter
import com.rhapp.presentation.viewmodel.EmpleadosAdminViewModel
import com.rhapp.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadosAdminScreen(
    viewModel: EmpleadosAdminViewModel = hiltViewModel(),
) {
    val state     by viewModel.state.collectAsState()
    val filtered  by viewModel.filtered.collectAsState()
    val formState by viewModel.formState.collectAsState()

    var showForm     by remember { mutableStateOf(false) }
    var editTarget   by remember { mutableStateOf<Empleado?>(null) }
    var deleteTarget by remember { mutableStateOf<Empleado?>(null) }
    var snackMsg     by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackMsg) {
        snackMsg?.let {
            snackbarHostState.showSnackbar(it)
            snackMsg = null
        }
    }

    Scaffold(
        snackbarHost   = { SnackbarHost(snackbarHostState) },
        containerColor = Background,
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
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
                                text       = "Empleados",
                                style      = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color      = TextPrimary,
                            )
                            Text(
                                text  = "${state.empleados.size} empleados · ${filtered.size} mostrados",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(onClick = viewModel::load) {
                                Icon(Icons.Default.Refresh, null, tint = TextSecondary)
                            }
                            Button(
                                onClick = { editTarget = null; showForm = true },
                                colors  = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = AccentOnDark),
                                shape   = MaterialTheme.shapes.medium,
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                            ) {
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Nuevo", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Búsqueda
                    OutlinedTextField(
                        value         = state.search,
                        onValueChange = viewModel::setSearch,
                        placeholder   = { Text("Buscar por nombre, apellido, cédula...", color = TextFaint) },
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

                    Spacer(Modifier.height(10.dp))

                    // FilterChips — Estado
                    Text("Estado", style = MaterialTheme.typography.labelSmall, color = TextFaint)
                    Spacer(Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(EmpleadoEstadoFilter.entries) { filter ->
                            FilterChip(
                                selected = state.estadoFilter == filter,
                                onClick  = { viewModel.setEstadoFilter(filter) },
                                label    = { Text(filter.label, style = MaterialTheme.typography.labelSmall) },
                                colors   = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = estadoColor(filter).copy(alpha = 0.85f),
                                    selectedLabelColor     = AccentOnDark,
                                    containerColor         = Surface2,
                                    labelColor             = TextSecondary,
                                ),
                            )
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    // FilterChips — Tipo Contrato
                    Text("Contrato", style = MaterialTheme.typography.labelSmall, color = TextFaint)
                    Spacer(Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(EmpleadoContratoFilter.entries) { filter ->
                            FilterChip(
                                selected = state.contratoFilter == filter,
                                onClick  = { viewModel.setContratoFilter(filter) },
                                label    = { Text(filter.label, style = MaterialTheme.typography.labelSmall) },
                                colors   = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Accent,
                                    selectedLabelColor     = AccentOnDark,
                                    containerColor         = Surface2,
                                    labelColor             = TextSecondary,
                                ),
                            )
                        }
                    }
                }
            }

            // ── Contenido ───────────────────────────────────────────
            when {
                state.isLoading -> LoadingScreen("Cargando empleados...")
                state.error != null -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⚠️", fontSize = 40.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(state.error!!, style = MaterialTheme.typography.bodyMedium, color = Error)
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = viewModel::load) { Text("Reintentar") }
                        }
                    }
                }
                filtered.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("👤", fontSize = 48.sp)
                            Spacer(Modifier.height(12.dp))
                            Text(
                                if (state.search.isBlank() && state.estadoFilter == EmpleadoEstadoFilter.TODOS && state.contratoFilter == EmpleadoContratoFilter.TODOS)
                                    "Sin empleados registrados"
                                else
                                    "Sin resultados para los filtros aplicados",
                                style      = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color      = TextPrimary,
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier            = Modifier.fillMaxSize(),
                        contentPadding      = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(filtered, key = { it.id }) { emp ->
                            EmpleadoAdminCard(
                                empleado = emp,
                                onEdit   = { editTarget = emp; showForm = true },
                                onDelete = { deleteTarget = emp },
                            )
                        }
                    }
                }
            }
        }
    }

    // ── Bottom Sheet formulario ─────────────────────────────────
    if (showForm) {
        EmpleadoFormSheet(
            initial   = editTarget,
            puestos   = state.puestos,
            formState = formState,
            onSave    = { payload ->
                if (editTarget != null) viewModel.updateEmpleado(editTarget!!.id, payload)
                else viewModel.createEmpleado(payload)
            },
            onDismiss = {
                showForm   = false
                editTarget = null
                viewModel.resetFormState()
            },
        )
    }

    // ── Diálogo de confirmación de eliminación ──────────────────
    deleteTarget?.let { emp ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            containerColor   = Surface,
            shape            = MaterialTheme.shapes.large,
            title = { Text("¿Eliminar empleado?", color = TextPrimary) },
            text  = {
                Text(
                    "\"${emp.nombre} ${emp.apellido}\" se eliminará permanentemente. " +
                    "Esta acción no se puede deshacer.",
                    color = TextSecondary,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteEmpleado(emp.id) { msg -> snackMsg = msg }
                    deleteTarget = null
                }) {
                    Text("Eliminar", color = Error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
        )
    }
}

// ── Helper: color del chip según estado ───────────────────────

@Composable
private fun estadoColor(filter: EmpleadoEstadoFilter) = when (filter.value) {
    EstadoEmpleado.ACTIVO      -> StatusActivo
    EstadoEmpleado.EN_LICENCIA -> StatusLicencia
    EstadoEmpleado.SUSPENDIDO  -> StatusSuspendido
    EstadoEmpleado.INACTIVO    -> StatusInactivo
    null                       -> Accent
}

// ── EmpleadoAdminCard ──────────────────────────────────────────

@Composable
private fun EmpleadoAdminCard(
    empleado: Empleado,
    onEdit:   () -> Unit,
    onDelete: () -> Unit,
) {
    val estadoBadgeColor = when (empleado.estado) {
        EstadoEmpleado.ACTIVO      -> StatusActivo
        EstadoEmpleado.EN_LICENCIA -> StatusLicencia
        EstadoEmpleado.SUSPENDIDO  -> StatusSuspendido
        EstadoEmpleado.INACTIVO    -> StatusInactivo
    }

    Surface(
        shape    = MaterialTheme.shapes.large,
        color    = Surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Avatar con inicial
            Box(
                modifier         = Modifier
                    .size(48.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(listOf(Accent, AccentLight)),
                        shape = MaterialTheme.shapes.medium,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text       = empleado.nombre.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    color      = AccentOnDark,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 20.sp,
                )
            }

            Spacer(Modifier.width(12.dp))

            // Info principal
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text       = "${empleado.nombre} ${empleado.apellido}",
                        style      = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextPrimary,
                    )
                    // Badge de estado
                    Surface(color = estadoBadgeColor.copy(alpha = 0.15f), shape = MaterialTheme.shapes.extraSmall) {
                        Text(
                            text       = empleado.estado.label,
                            color      = estadoBadgeColor,
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                }
                Text(
                    text  = "${empleado.puestoTitulo} · ${empleado.departamentoNombre}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text  = "# ${empleado.numeroEmpleado}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextFaint,
                    )
                    Text(
                        text       = "S/ ${"%,.0f".format(empleado.salarioActual)}",
                        style      = MaterialTheme.typography.bodySmall,
                        color      = Accent,
                        fontWeight = FontWeight.SemiBold,
                    )
                    // Badge tipo contrato
                    Surface(color = Surface2, shape = MaterialTheme.shapes.extraSmall) {
                        Text(
                            text     = empleado.tipoContrato.label,
                            color    = TextSecondary,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                        )
                    }
                }
            }

            // Acciones
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = TextSecondary, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Error, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
