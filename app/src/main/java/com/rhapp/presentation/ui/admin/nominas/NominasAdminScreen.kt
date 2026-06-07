package com.rhapp.presentation.ui.admin.nominas

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
import com.rhapp.domain.model.Nomina
import com.rhapp.presentation.components.LoadingScreen
import com.rhapp.presentation.viewmodel.NominaEstadoFilter
import com.rhapp.presentation.viewmodel.NominasAdminViewModel
import com.rhapp.theme.*

private val MESES = listOf(
    null to "Todos", 1 to "Ene", 2 to "Feb", 3 to "Mar", 4 to "Abr",
    5 to "May", 6 to "Jun", 7 to "Jul", 8 to "Ago",
    9 to "Sep", 10 to "Oct", 11 to "Nov", 12 to "Dic",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NominasAdminScreen(
    viewModel: NominasAdminViewModel = hiltViewModel(),
) {
    val state     by viewModel.state.collectAsState()
    val filtered  by viewModel.filtered.collectAsState()
    val formState by viewModel.formState.collectAsState()

    var showForm     by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<Nomina?>(null) }
    var detailTarget by remember { mutableStateOf<Nomina?>(null) }
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
                            Text("Nóminas", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("${state.nominas.size} nóminas · ${state.anioFilter}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
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
                                Text("Nueva", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Búsqueda
                    OutlinedTextField(
                        value = state.search, onValueChange = viewModel::setSearch,
                        placeholder = { Text("Buscar por empleado...", color = TextFaint) },
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
                        items(NominaEstadoFilter.entries) { f ->
                            val chipColor = if (f.value != null) nominaEstadoColor(f.value) else Accent
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

                    Spacer(Modifier.height(6.dp))

                    // FilterChips — Mes
                    Text("Mes", style = MaterialTheme.typography.labelSmall, color = TextFaint)
                    Spacer(Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(MESES) { (mes, label) ->
                            FilterChip(
                                selected = state.mesFilter == mes,
                                onClick  = { viewModel.setMesFilter(mes) },
                                label    = { Text(label, style = MaterialTheme.typography.labelSmall) },
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

            // ── Contenido ──────────────────────────────────────────
            when {
                state.isLoading -> LoadingScreen("Cargando nóminas...")
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
                            Text("💰", fontSize = 48.sp)
                            Spacer(Modifier.height(12.dp))
                            Text("Sin nóminas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("para los filtros seleccionados", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(filtered, key = { it.id }) { nomina ->
                            NominaAdminCard(
                                nomina   = nomina,
                                onPagada = { viewModel.marcarPagada(nomina.id) },
                                onDetail = { detailTarget = nomina },
                                onDelete = { deleteTarget = nomina },
                            )
                        }
                    }
                }
            }
        }
    }

    // ── Form: generar nueva nómina ─────────────────────────────
    if (showForm) {
        NominaFormSheet(
            empleados = state.empleados,
            formState = formState,
            onSave    = viewModel::createNomina,
            onDismiss = { showForm = false; viewModel.resetFormState() },
        )
    }

    // ── Sheet de detalle ────────────────────────────────────────
    detailTarget?.let { nomina ->
        NominaDetailSheet(nomina = nomina, onDismiss = { detailTarget = null })
    }

    // ── Diálogo eliminar ────────────────────────────────────────
    deleteTarget?.let { nomina ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            containerColor   = Surface,
            shape            = MaterialTheme.shapes.large,
            title = { Text("¿Eliminar nómina?", color = TextPrimary) },
            text  = {
                Text(
                    "Nómina de ${nomina.empleadoNombre} — ${nomina.mes}/${nomina.anio}. Esta acción no se puede deshacer.",
                    color = TextSecondary,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteNomina(nomina.id) { msg -> snackMsg = msg }
                    deleteTarget = null
                }) { Text("Eliminar", color = Error, fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("Cancelar", color = TextSecondary) } },
        )
    }
}

// ── NominaAdminCard ────────────────────────────────────────────

@Composable
private fun NominaAdminCard(
    nomina:   Nomina,
    onPagada: () -> Unit,
    onDetail: () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        onClick = onDetail,
        shape   = MaterialTheme.shapes.large,
        color   = Surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(nomina.empleadoNombre, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text(
                        "Período: ${nomina.mes.toString().padStart(2, '0')}/${nomina.anio}",
                        style = MaterialTheme.typography.bodySmall, color = TextSecondary,
                    )
                }
                EstadoNominaDropdown(current = nomina.estado, onPagada = onPagada)
            }
            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Text(
                    "S/ ${"%.2f".format(nomina.salarioNeto)}",
                    style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = NominaPagada,
                )
                Row {
                    IconButton(onClick = onDetail, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Info, null, tint = Accent, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, null, tint = Error, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
