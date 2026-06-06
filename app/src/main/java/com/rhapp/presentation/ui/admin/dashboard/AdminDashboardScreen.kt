package com.rhapp.presentation.ui.admin.dashboard

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
import com.rhapp.domain.model.EstadoNomina
import com.rhapp.presentation.components.KpiCard
import com.rhapp.presentation.components.LoadingScreen
import com.rhapp.presentation.viewmodel.AdminDashboardUiState
import com.rhapp.presentation.viewmodel.AdminDashboardViewModel
import com.rhapp.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    onNavigate: (String) -> Unit,
    viewModel:  AdminDashboardViewModel = hiltViewModel(),
) {
    val state       by viewModel.state.collectAsState()
    val lastUpdated by viewModel.lastUpdated.collectAsState()

    when (val s = state) {
        is AdminDashboardUiState.Loading ->
            LoadingScreen("Cargando dashboard...")
        is AdminDashboardUiState.Error   -> {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("⚠️ ${s.message}", color = Error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = viewModel::load,
                        colors = ButtonDefaults.buttonColors(containerColor = Accent)) {
                        Text("Reintentar", color = AccentOnDark)
                    }
                }
            }
        }
        is AdminDashboardUiState.Success ->
            AdminDashboardContent(
                stats       = s.stats,
                lastUpdated = lastUpdated,
                onNavigate  = onNavigate,
                onRefresh   = viewModel::load,
            )
    }
}

@Composable
private fun AdminDashboardContent(
    stats:       com.rhapp.presentation.viewmodel.AdminDashboardStats,
    lastUpdated: Long,
    onNavigate:  (String) -> Unit,
    onRefresh:   () -> Unit,
) {
    val timeFmt = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val timeStr = if (lastUpdated > 0) timeFmt.format(Date(lastUpdated)) else "—"

    LazyColumn(
        modifier       = Modifier.fillMaxSize().background(Background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header
        item {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text       = "Dashboard Admin",
                        style      = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary,
                    )
                    Text(
                        text  = "Actualizado: $timeStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextFaint,
                    )
                }
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = Accent)
                }
            }
        }

        // ── KPIs — fila 1 ─────────────────────────────────────
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiCard(
                    title    = "Departamentos",
                    value    = stats.departamentosActivos.toString(),
                    subtitle = "${stats.totalDepartamentos} total",
                    icon     = Icons.Default.Business,
                    color    = Info,
                    onClick  = { onNavigate("admin/departamentos") },
                    modifier = Modifier.weight(1f),
                )
                KpiCard(
                    title    = "Puestos activos",
                    value    = stats.puestosActivos.toString(),
                    subtitle = "${stats.totalPuestos} total",
                    icon     = Icons.Default.Work,
                    color    = Success,
                    onClick  = { onNavigate("admin/puestos") },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // ── KPIs — fila 2 ─────────────────────────────────────
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiCard(
                    title    = "Nóminas pendientes",
                    value    = stats.nominasPendientes.toString(),
                    subtitle = "${stats.totalNominas} total",
                    icon     = Icons.Default.Payments,
                    color    = Warning,
                    hasAlert = stats.nominasPendientes > 0,
                    onClick  = { onNavigate("admin/nominas") },
                    modifier = Modifier.weight(1f),
                )
                KpiCard(
                    title    = "Nóminas pagadas",
                    value    = stats.nominasPagadas.toString(),
                    subtitle = "${stats.totalNominas} total",
                    icon     = Icons.Default.CheckCircle,
                    color    = StatusActivo,
                    onClick  = { onNavigate("admin/nominas") },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // ── Nóminas por estado ───────────────────────────────
        if (stats.nominasPorEstado.isNotEmpty()) {
            item {
                Surface(
                    color    = Surface,
                    shape    = MaterialTheme.shapes.large,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically,
                        ) {
                            Text(
                                text       = "Nóminas por estado",
                                style      = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color      = TextPrimary,
                            )
                            TextButton(onClick = { onNavigate("admin/nominas") }) {
                                Text("Ver todas", color = Accent,
                                    style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Spacer(Modifier.height(16.dp))

                        val total = stats.nominasPorEstado.values.sum().coerceAtLeast(1)
                        stats.nominasPorEstado.entries.forEach { (estadoValue, count) ->
                            val estado = EstadoNomina.fromValue(estadoValue)
                            val color = when (estado) {
                                EstadoNomina.GENERADA -> NominaGenerada
                                EstadoNomina.REVISADA -> NominaRevisada
                                EstadoNomina.PAGADA   -> NominaPagada
                                EstadoNomina.ANULADA  -> NominaAnulada
                            }
                            val pct = (count.toFloat() / total).coerceIn(0.02f, 1f)

                            Column(modifier = Modifier.padding(bottom = 10.dp)) {
                                Row(
                                    modifier              = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text  = estado.label,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                    )
                                    Text(
                                        text       = count.toString(),
                                        style      = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color      = color,
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(7.dp)
                                        .background(Surface2, MaterialTheme.shapes.extraSmall),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(pct)
                                            .fillMaxHeight()
                                            .background(color, MaterialTheme.shapes.extraSmall),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── Acciones rápidas ──────────────────────────────────
        item {
            Surface(
                color    = Surface,
                shape    = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text       = "⚡ Acciones rápidas",
                        style      = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary,
                        modifier   = Modifier.padding(bottom = 12.dp),
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(listOf(
                            Triple("+ Depto",      Info,   "admin/departamentos"),
                            Triple("+ Puesto",     Success,"admin/puestos"),
                            Triple("Ver Nóminas", Warning,"admin/nominas"),
                            Triple("Empleados",    Accent,  "admin/empleados"),
                            Triple("Asistencia",  StatusActivo,"admin/asistencias"),
                        )) { (label, color, route) ->
                            Surface(
                                onClick  = { onNavigate(route) },
                                color    = color.copy(alpha = 0.1f),
                                shape    = MaterialTheme.shapes.medium,
                            ) {
                                Text(
                                    text       = label,
                                    color      = color,
                                    fontWeight = FontWeight.Bold,
                                    style      = MaterialTheme.typography.bodySmall,
                                    modifier   = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
