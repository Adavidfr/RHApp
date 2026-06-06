package com.rhapp.presentation.ui.nominas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rhapp.domain.model.EstadoNomina
import com.rhapp.domain.model.Nomina
import com.rhapp.presentation.components.ErrorScreen
import com.rhapp.presentation.components.LoadingScreen
import com.rhapp.presentation.components.StatusBadge
import com.rhapp.presentation.viewmodel.NominaDetailUiState
import com.rhapp.presentation.viewmodel.NominaDetailViewModel
import com.rhapp.theme.*
import java.text.SimpleDateFormat
import java.util.Locale

private val PROGRESS_STEPS = listOf(
    EstadoNomina.GENERADA,
    EstadoNomina.REVISADA,
    EstadoNomina.PAGADA,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NominaDetailScreen(
    nominaId:   Int,
    onBack:     () -> Unit,
    viewModel:  NominaDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(nominaId) { viewModel.load(nominaId) }

    when (val s = state) {
        is NominaDetailUiState.Loading ->
            LoadingScreen("Cargando nómina...")
        is NominaDetailUiState.Error   ->
            ErrorScreen(s.message, onRetry = { viewModel.load(nominaId) })
        is NominaDetailUiState.Success ->
            NominaDetailContent(nomina = s.nomina, onBack = onBack)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NominaDetailContent(nomina: Nomina, onBack: () -> Unit) {
    val inputFmt  = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
    val outputFmt = SimpleDateFormat("dd MMM yyyy · HH:mm", Locale("es"))
    val dateStr   = runCatching { outputFmt.format(inputFmt.parse(nomina.fechaGeneracion)!!) }
                        .getOrDefault(nomina.fechaGeneracion.take(16))

    val isCancelled = nomina.estado == EstadoNomina.ANULADA
    val currentStep = PROGRESS_STEPS.indexOf(nomina.estado).coerceAtLeast(0)

    val mesAnio = "${getMesNombre(nomina.mes)} ${nomina.anio}"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Nómina - $mesAnio",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color      = TextPrimary,
                        )
                        Text(dateStr, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
                    }
                },
                actions = { StatusBadge(nomina.estado, modifier = Modifier.padding(end = 16.dp)) },
                colors  = TopAppBarDefaults.topAppBarColors(containerColor = Surface),
            )
        },
        containerColor = Background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            // ── Barra de progreso ──────────────────────────────
            if (!isCancelled) {
                NominaProgressBar(
                    steps       = PROGRESS_STEPS,
                    currentStep = currentStep,
                )
            } else {
                Surface(
                    color    = Error.copy(alpha = 0.08f),
                    shape    = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text     = "⚠️ Esta nómina fue anulada",
                        color    = Error,
                        fontWeight = FontWeight.SemiBold,
                        style    = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }

            // ── Información del empleado ───────────────────────
            SectionCard(title = "Empleado") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoRow("Nombre", nomina.empleadoNombre)
                    HorizontalDivider(color = Border, thickness = 0.5.dp)
                    InfoRow("ID Empleado", nomina.empleadoId.toString())
                }
            }

            // ── Desglose de salarios ───────────────────────────
            SectionCard(title = "Desglose salarial") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TotalLine("Salario base", nomina.salarioBase, false)
                    TotalLine("Bono", nomina.bono, false)
                    HorizontalDivider(color = Border, thickness = 0.5.dp)
                    TotalLine("Total bruto", nomina.salarioBase + nomina.bono, false)
                }
            }

            // ── Descuentos ─────────────────────────────────────
            SectionCard(title = "Descuentos") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TotalLine("Aportes", nomina.descuentoAportes, false)
                    TotalLine("Impuestos", nomina.descuentoImpuestos, false)
                    HorizontalDivider(color = Border, thickness = 0.5.dp)
                    val totalDescuentos = nomina.descuentoAportes + nomina.descuentoImpuestos
                    TotalLine("Total descuentos", totalDescuentos, false)
                }
            }

            // ── Resumen final ───────────────────────────────────
            SectionCard(title = "Resumen") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TotalLine("Salario neto", nomina.salarioNeto, true)
                }
            }

            // ── Fechas importantes ─────────────────────────────
            SectionCard(title = "Fechas") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoRow("Generación", dateStr)
                    if (nomina.fechaPago != null) {
                        HorizontalDivider(color = Border, thickness = 0.5.dp)
                        val pagoFmt = SimpleDateFormat("dd MMM yyyy", Locale("es"))
                        val pagoStr = runCatching { pagoFmt.format(inputFmt.parse(nomina.fechaPago)!!) }
                                       .getOrDefault(nomina.fechaPago.take(10))
                        InfoRow("Pago", pagoStr)
                    }
                }
            }
        }
    }
}

// ── Barra de progreso ─────────────────────────────────────────

@Composable
private fun NominaProgressBar(steps: List<EstadoNomina>, currentStep: Int) {
    Surface(
        color    = Surface,
        shape    = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text       = "Estado de la nómina",
                style      = MaterialTheme.typography.labelSmall,
                color      = TextSecondary,
                letterSpacing = 0.8.sp,
                modifier   = Modifier.padding(bottom = 20.dp),
            )
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                steps.forEachIndexed { index, step ->
                    val isDone    = index <= currentStep
                    val isCurrent = index == currentStep
                    val color     = if (isDone) Accent else Border

                    // Nodo
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier         = Modifier
                                .size(if (isCurrent) 36.dp else 30.dp)
                                .background(
                                    if (isDone) Accent else Surface2,
                                    CircleShape,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text       = if (isDone) "✓" else "${index + 1}",
                                color      = if (isDone) AccentOnDark else TextFaint,
                                fontSize   = if (isCurrent) 14.sp else 12.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text       = step.label,
                            style      = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color      = if (isDone) Accent else TextFaint,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        )
                    }

                    // Línea conectora
                    if (index < steps.lastIndex) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .padding(bottom = 20.dp)
                                .background(if (index < currentStep) Accent else Border),
                        )
                    }
                }
            }
        }
    }
}

// ── Sub-componentes ───────────────────────────────────────────

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(color = Surface, shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text       = title,
                style      = MaterialTheme.typography.labelSmall,
                color      = TextSecondary,
                letterSpacing = 0.8.sp,
                modifier   = Modifier.padding(bottom = 14.dp),
            )
            content()
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
        Text(
            text       = value,
            style      = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color      = TextPrimary,
        )
    }
}

@Composable
private fun TotalLine(label: String, value: Double, isFinal: Boolean) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text       = label,
            style      = if (isFinal) MaterialTheme.typography.titleMedium
                         else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isFinal) FontWeight.Bold else FontWeight.Normal,
            color      = if (isFinal) TextPrimary else TextSecondary,
        )
        Text(
            text       = "$${"%.2f".format(value)}",
            style      = if (isFinal) MaterialTheme.typography.titleMedium
                         else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isFinal) FontWeight.ExtraBold else FontWeight.SemiBold,
            color      = if (isFinal) Accent else TextPrimary,
        )
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
