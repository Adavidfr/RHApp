package com.rhapp.presentation.ui.admin.asistencias

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rhapp.domain.model.Asistencia
import com.rhapp.domain.model.EstadoAsistencia
import com.rhapp.presentation.components.LoadingScreen
import com.rhapp.presentation.viewmodel.AsistenciaEstadoFilter
import com.rhapp.presentation.viewmodel.AsistenciasAdminViewModel
import com.rhapp.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsistenciasAdminScreen(
    viewModel: AsistenciasAdminViewModel = hiltViewModel(),
) {
    val state     by viewModel.state.collectAsState()
    val filtered  by viewModel.filtered.collectAsState()
    val formState by viewModel.formState.collectAsState()

    var showForm     by remember { mutableStateOf(false) }
    var salidaTarget by remember { mutableStateOf<Asistencia?>(null) }
    var snackMsg     by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(snackMsg) { snackMsg?.let { snackbarHostState.showSnackbar(it); snackMsg = null } }

    Scaffold(
        snackbarHost   = { SnackbarHost(snackbarHostState) },
        containerColor = Background,
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(scaffoldPadding).background(Background),
        ) {
            // ── Header ─────────────────────────────────────────────
            Surface(color = Surface, tonalElevation = 0.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text("Asistencia", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("${filtered.size} de ${state.asistencias.size} · ${state.fechaFilter}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(onClick = viewModel::load) { Icon(Icons.Default.Refresh, null, tint = TextSecondary) }
                            Button(
                                onClick = { showForm = true },
                                colors  = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = AccentOnDark),
                                shape   = MaterialTheme.shapes.medium,
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                            ) {
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Registrar", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Campo de fecha
                    OutlinedTextField(
                        value = state.fechaFilter,
                        onValueChange = { if (Regex("""\d{4}-\d{2}-\d{2}""").matches(it)) viewModel.setFecha(it) else viewModel.setFecha(it) },
                        label = { Text("Fecha") },
                        placeholder = { Text("YYYY-MM-DD", color = TextFaint) },
                        leadingIcon = { Icon(Icons.Default.DateRange, null, tint = TextSecondary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Accent, unfocusedBorderColor = Border, cursorColor = Accent,
                        ),
                        trailingIcon = {
                            if (state.fechaFilter.isNotBlank()) {
                                IconButton(onClick = viewModel::load) {
                                    Icon(Icons.Default.Search, null, tint = Accent)
                                }
                            }
                        },
                    )

                    Spacer(Modifier.height(10.dp))

                    // Búsqueda por nombre
                    OutlinedTextField(
                        value = state.search, onValueChange = viewModel::setSearch,
                        placeholder = { Text("Buscar empleado...", color = TextFaint) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Accent, unfocusedBorderColor = Border, cursorColor = Accent),
                    )

                    Spacer(Modifier.height(10.dp))

                    // FilterChips — Estado
                    Text("Estado", style = MaterialTheme.typography.labelSmall, color = TextFaint)
                    Spacer(Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(AsistenciaEstadoFilter.entries) { f ->
                            val chipColor = asistenciaEstadoColor(f.value)
                            FilterChip(
                                selected = state.estadoFilter == f,
                                onClick  = { viewModel.setEstadoFilter(f) },
                                label    = { Text(f.label, style = MaterialTheme.typography.labelSmall) },
                                colors   = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = chipColor.copy(alpha = 0.85f),
                                    selectedLabelColor     = AccentOnDark,
                                    containerColor         = Surface2,
                                    labelColor             = TextSecondary,
                                ),
                            )
                        }
                    }
                }
            }

            // ── Contenido ──────────────────────────────────────────
            when {
                state.isLoading -> LoadingScreen("Cargando asistencias...")
                state.error != null -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⚠️", fontSize = 40.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(state.error!!, color = Error, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = viewModel::load) { Text("Reintentar") }
                        }
                    }
                }
                filtered.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📋", fontSize = 48.sp)
                            Spacer(Modifier.height(12.dp))
                            Text("Sin registros", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("para la fecha ${state.fechaFilter}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(filtered, key = { it.id }) { asist ->
                            AsistenciaAdminCard(
                                asistencia    = asist,
                                onRegistrarSalida = {
                                    if (asist.horaSalida == null && asist.status == EstadoAsistencia.PRESENTE) {
                                        salidaTarget = asist
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    // ── Form: registrar entrada ─────────────────────────────────
    if (showForm) {
        AsistenciaFormSheet(
            empleados    = state.empleados,
            fechaDefault = state.fechaFilter,
            formState    = formState,
            onSave       = viewModel::registrarEntrada,
            onDismiss    = { showForm = false; viewModel.resetFormState() },
        )
    }

    // ── Diálogo: confirmar salida ───────────────────────────────
    salidaTarget?.let { asist ->
        AlertDialog(
            onDismissRequest = { salidaTarget = null },
            containerColor   = Surface,
            shape            = MaterialTheme.shapes.large,
            title = { Text("Registrar salida", color = TextPrimary) },
            text  = {
                Text(
                    "¿Confirmas la salida de ${asist.empleadoNombre} en la fecha ${asist.fecha}?",
                    color = TextSecondary,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.registrarSalida(asist.id) { msg -> snackMsg = msg }
                        salidaTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = AccentOnDark),
                ) { Text("Confirmar salida", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { salidaTarget = null }) { Text("Cancelar", color = TextSecondary) } },
        )
    }
}

// ── Helper de color ────────────────────────────────────────────

fun asistenciaEstadoColor(estado: EstadoAsistencia?) = when (estado) {
    EstadoAsistencia.PRESENTE          -> StatusActivo
    EstadoAsistencia.AUSENTE           -> Error
    EstadoAsistencia.LICENCIA          -> StatusLicencia
    EstadoAsistencia.RETARDO           -> Warning
    EstadoAsistencia.SALIDA_ANTICIPADA -> TextSecondary
    null                               -> Accent
}

// ── AsistenciaAdminCard ────────────────────────────────────────

@Composable
private fun AsistenciaAdminCard(
    asistencia:        Asistencia,
    onRegistrarSalida: () -> Unit,
) {
    val color = asistenciaEstadoColor(asistencia.status)
    val canClose = asistencia.horaSalida == null && asistencia.status == EstadoAsistencia.PRESENTE

    Surface(
        shape    = MaterialTheme.shapes.large,
        color    = Surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Indicador de estado
            Box(
                modifier         = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.12f), MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = when (asistencia.status) {
                        EstadoAsistencia.PRESENTE          -> "✅"
                        EstadoAsistencia.AUSENTE           -> "❌"
                        EstadoAsistencia.LICENCIA          -> "📋"
                        EstadoAsistencia.RETARDO           -> "⏰"
                        EstadoAsistencia.SALIDA_ANTICIPADA -> "🏃"
                    },
                    fontSize = 20.sp,
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(asistencia.empleadoNombre, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Surface(color = color.copy(alpha = 0.15f), shape = MaterialTheme.shapes.extraSmall) {
                        Text(asistencia.status.label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    asistencia.horaEntrada?.let { Text("Entrada: $it", style = MaterialTheme.typography.bodySmall, color = TextSecondary) }
                    asistencia.horaSalida?.let  { Text("Salida: $it",  style = MaterialTheme.typography.bodySmall, color = TextSecondary) }
                }
                if (asistencia.minutosRetardo > 0) {
                    Text("${asistencia.minutosRetardo} min retardo", style = MaterialTheme.typography.bodySmall, color = Warning, fontWeight = FontWeight.SemiBold)
                }
                if (asistencia.horasTrabajadas > 0) {
                    Text("${"%.1f".format(asistencia.horasTrabajadas)} h trabajadas", style = MaterialTheme.typography.bodySmall, color = StatusActivo)
                }
            }

            // Acción: registrar salida (si aún no tiene y está presente)
            if (canClose) {
                IconButton(onClick = onRegistrarSalida) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Registrar salida", tint = Accent, modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}
