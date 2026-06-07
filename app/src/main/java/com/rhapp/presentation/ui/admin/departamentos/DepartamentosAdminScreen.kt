package com.rhapp.presentation.ui.admin.departamentos

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
import com.rhapp.domain.model.Departamento
import com.rhapp.presentation.components.LoadingScreen
import com.rhapp.presentation.viewmodel.DepartamentosAdminViewModel
import com.rhapp.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartamentosAdminScreen(
    viewModel: DepartamentosAdminViewModel = hiltViewModel(),
) {
    val state     by viewModel.state.collectAsState()
    val filtered  by viewModel.filtered.collectAsState()
    val formState by viewModel.formState.collectAsState()

    var showForm     by remember { mutableStateOf(false) }
    var editTarget   by remember { mutableStateOf<Departamento?>(null) }
    var deleteTarget by remember { mutableStateOf<Departamento?>(null) }

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
                            text       = "Departamentos",
                            style      = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color      = TextPrimary,
                        )
                        Text(
                            text  = "${state.departamentos.size} departamentos",
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
                    placeholder   = { Text("Buscar por nombre o código...", color = TextFaint) },
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
            state.isLoading -> LoadingScreen("Cargando departamentos...")
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
                        Text("🏢", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            if (state.search.isBlank()) "Sin departamentos" else "Sin resultados",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color      = TextPrimary,
                        )
                        if (state.search.isBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Crea el primer departamento",
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
                    items(filtered, key = { it.id }) { dep ->
                        DepartamentoAdminCard(
                            departamento = dep,
                            onToggle     = { viewModel.toggleActive(dep.id, !dep.activo) },
                            onEdit       = { editTarget = dep; showForm = true },
                            onDelete     = { deleteTarget = dep },
                        )
                    }
                }
            }
        }
    }

    // ── Bottom Sheet formulario ─────────────────────────────────
    if (showForm) {
        DepartamentoFormSheet(
            initial   = editTarget,
            formState = formState,
            onSave    = { payload ->
                if (editTarget != null) viewModel.updateDepartamento(editTarget!!.id, payload)
                else viewModel.createDepartamento(payload)
            },
            onDismiss = {
                showForm   = false
                editTarget = null
                viewModel.resetFormState()
            },
        )
    }

    // ── Diálogo de eliminación inteligente ──────────────────────
    deleteTarget?.let { dep ->
        val tieneEmpleados = dep.totalEmpleados > 0
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = {
                Text(
                    if (tieneEmpleados) "¿Desactivar departamento?" else "¿Eliminar departamento?",
                    color = TextPrimary,
                )
            },
            text = {
                Text(
                    if (tieneEmpleados)
                        "\"${dep.nombre}\" tiene ${dep.totalEmpleados} empleado(s). Se desactivará en lugar de eliminarse."
                    else
                        "\"${dep.nombre}\" se eliminará permanentemente.",
                    color = TextSecondary,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (tieneEmpleados) viewModel.toggleActive(dep.id, false)
                    else viewModel.deleteDepartamento(dep.id)
                    deleteTarget = null
                }) {
                    Text(
                        if (tieneEmpleados) "Desactivar" else "Eliminar",
                        color      = Error,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = Surface,
            shape          = MaterialTheme.shapes.large,
        )
    }
}

// ── DepartamentoAdminCard ──────────────────────────────────────

@Composable
private fun DepartamentoAdminCard(
    departamento: Departamento,
    onToggle:     () -> Unit,
    onEdit:       () -> Unit,
    onDelete:     () -> Unit,
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
                checked         = departamento.activo,
                onCheckedChange = { onToggle() },
                colors          = SwitchDefaults.colors(
                    checkedThumbColor    = AccentOnDark,
                    checkedTrackColor    = Accent,
                    uncheckedTrackColor  = Surface2,
                    uncheckedBorderColor = Border,
                ),
            )

            Spacer(Modifier.width(12.dp))

            // Info del departamento
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text       = departamento.nombre,
                        style      = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color      = TextPrimary,
                    )
                    if (!departamento.activo) {
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
                    text  = departamento.codigo,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextFaint,
                )
                if (departamento.jefeNombre != null) {
                    Text(
                        text  = "👤 ${departamento.jefeNombre}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
                Text(
                    text       = "${departamento.totalEmpleados} empleados",
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
