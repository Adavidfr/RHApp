package com.rhapp.presentation.ui.nominas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rhapp.domain.model.EstadoNomina
import com.rhapp.presentation.components.StatusBadge
import com.rhapp.presentation.components.LoadingScreen
import com.rhapp.presentation.components.ErrorScreen
import com.rhapp.presentation.viewmodel.NominasViewModel
import com.rhapp.theme.*
import java.text.SimpleDateFormat
import java.util.Locale

private val STATUS_FILTERS = listOf(
    "" to "Todas",
    EstadoNomina.GENERADA.value to "Generadas",
    EstadoNomina.REVISADA.value to "Revisadas",
    EstadoNomina.PAGADA.value   to "Pagadas",
    EstadoNomina.ANULADA.value  to "Anuladas",
)

@Composable
fun NominasScreen(
    onNominaClick: (Int) -> Unit,
    viewModel:     NominasViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        // ── Header ────────────────────────────────────────────
        Surface(color = Surface, tonalElevation = 0.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text       = "Mis nóminas",
                            style      = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color      = TextPrimary,
                        )
                        Text(
                            text  = if (state.isLoading) "..." else "${state.nominas.size} nóminas",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    }
                    IconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = TextSecondary)
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Filtros por estado
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(STATUS_FILTERS) { (value, label) ->
                        FilterChip(
                            selected = state.statusFilter == value,
                            onClick  = { viewModel.setStatusFilter(value) },
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

        // ── Contenido ─────────────────────────────────────────
        when {
            state.isLoading && state.nominas.isEmpty() ->
                LoadingScreen("Cargando nóminas...")

            state.error != null && state.nominas.isEmpty() ->
                ErrorScreen(state.error!!, onRetry = viewModel::refresh)

            state.nominas.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("💰", fontSize = 52.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text       = if (state.statusFilter.isBlank()) "Sin nóminas"
                                         else "Sin nóminas con este estado",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color      = TextPrimary,
                        )
                        Text(
                            text  = "Tus nóminas aparecerán aquí",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier       = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.nominas, key = { it.id }) { nomina ->
                        NominaCard(nomina = nomina, onClick = { onNominaClick(nomina.id) })
                    }
                }
            }
        }
    }
}

// ── NominaCard ─────────────────────────────────────────────────

@Composable
fun NominaCard(nomina: com.rhapp.domain.model.Nomina, onClick: () -> Unit) {
    val inputFmt  = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
    val outputFmt = SimpleDateFormat("dd MMM yyyy", Locale("es"))
    val dateStr   = runCatching {
        outputFmt.format(inputFmt.parse(nomina.fechaGeneracion)!!)
    }.getOrDefault(nomina.fechaGeneracion.take(10))

    // Formatear mes/año
    val mesAnio = "${getMesNombre(nomina.mes)} ${nomina.anio}"

    Surface(
        onClick        = onClick,
        shape          = MaterialTheme.shapes.large,
        color          = Surface,
        tonalElevation = 0.dp,
        modifier       = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top,
            ) {
                Column {
                    Text(
                        text       = "Nómina - $mesAnio",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary,
                    )
                    Text(
                        text  = dateStr,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
                StatusBadge(status = nomina.estado)
            }

            Spacer(Modifier.height(12.dp))

            // Info del empleado
            Text(
                text  = nomina.empleadoNombre,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
            )

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = BorderLight, thickness = 0.5.dp)
            Spacer(Modifier.height(10.dp))

            // Footer
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Text(
                    text  = "Salario neto",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
                Text(
                    text       = "$${"%.2f".format(nomina.salarioNeto)}",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Accent,
                )
            }
        }
    }
}

private fun getMesNombre(mes: Int): String {
    return when (mes) {
        1 -> "Enero"
        2 -> "Febrero"
        3 -> "Marzo"
        4 -> "Abril"
        5 -> "Mayo"
        6 -> "Junio"
        7 -> "Julio"
        8 -> "Agosto"
        9 -> "Septiembre"
        10 -> "Octubre"
        11 -> "Noviembre"
        12 -> "Diciembre"
        else -> "Mes $mes"
    }
}
