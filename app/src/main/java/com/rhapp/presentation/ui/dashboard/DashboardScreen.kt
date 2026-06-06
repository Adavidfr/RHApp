package com.rhapp.presentation.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rhapp.presentation.navigation.Screen
import com.rhapp.presentation.viewmodel.AuthViewModel
import com.rhapp.presentation.viewmodel.DashboardViewModel
import com.rhapp.theme.*

@Composable
fun DashboardScreen(
    onNavigate:    (String) -> Unit,
    onLogout:      () -> Unit,
    authViewModel: AuthViewModel,
    viewModel:     DashboardViewModel = hiltViewModel(),
) {
    val stats       by viewModel.stats.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState()),
    ) {
        // ── Encabezado ────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface)
                .padding(horizontal = 24.dp, vertical = 32.dp),
        ) {
            Column {
                Text(
                    text       = "Bienvenido,",
                    color      = TextSecondary,
                    style      = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text       = currentUser?.username ?: "Administrador",
                    fontSize   = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Accent,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = "Panel de Control — Recursos Humanos",
                    color = TextFaint,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Estadísticas rápidas ──────────────────────────────
        Text(
            text     = "Resumen del día",
            style    = MaterialTheme.typography.labelSmall,
            color    = TextSecondary,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        )

        if (stats.isLoading) {
            Box(
                modifier         = Modifier.fillMaxWidth().height(120.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Accent, strokeWidth = 2.dp)
            }
        } else {
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatCard(
                    label    = "Empleados activos",
                    value    = stats.totalEmpleados.toString(),
                    icon     = Icons.Default.People,
                    color    = StatusActivo,
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    label    = "Presentes hoy",
                    value    = stats.presentesHoy.toString(),
                    icon     = Icons.Default.HowToReg,
                    color    = Info,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatCard(
                    label    = "Departamentos",
                    value    = stats.totalDepartamentos.toString(),
                    icon     = Icons.Default.Business,
                    color    = Accent,
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    label    = "Nóminas pendientes",
                    value    = stats.nominasPendientes.toString(),
                    icon     = Icons.Default.Payments,
                    color    = if (stats.nominasPendientes > 0) Warning else Success,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        // ── Accesos rápidos ───────────────────────────────────
        Text(
            text     = "Gestión",
            style    = MaterialTheme.typography.labelSmall,
            color    = TextSecondary,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        )

        Column(
            modifier              = Modifier.padding(horizontal = 16.dp),
            verticalArrangement   = Arrangement.spacedBy(12.dp),
        ) {
            QuickAccessCard(
                title    = "Empleados",
                subtitle = "Gestionar personal activo",
                icon     = Icons.Default.People,
                color    = Accent,
                onClick  = { onNavigate(Screen.AdminEmpleados.route) },
            )
            QuickAccessCard(
                title    = "Departamentos & Puestos",
                subtitle = "Estructura organizacional",
                icon     = Icons.Default.Business,
                color    = Info,
                onClick  = { onNavigate(Screen.AdminDepartamentos.route) },
            )
            QuickAccessCard(
                title    = "Nóminas",
                subtitle = "Control de pagos y salarios",
                icon     = Icons.Default.Payments,
                color    = Success,
                onClick  = { onNavigate(Screen.AdminNominas.route) },
            )
            QuickAccessCard(
                title    = "Asistencia",
                subtitle = "Registro de entrada y salida",
                icon     = Icons.Default.CalendarMonth,
                color    = Warning,
                onClick  = { onNavigate(Screen.AdminAsistencias.route) },
            )
        }

        Spacer(Modifier.height(28.dp))

        // ── Cerrar sesión ─────────────────────────────────────
        OutlinedButton(
            onClick  = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(48.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Error),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(Error.copy(alpha = 0.5f)),
            ),
            shape = MaterialTheme.shapes.medium,
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Cerrar sesión", style = MaterialTheme.typography.labelLarge)
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun StatCard(
    label:    String,
    value:    String,
    icon:     ImageVector,
    color:    Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape   = MaterialTheme.shapes.large,
        color   = Surface,
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = color,
                modifier           = Modifier.size(24.dp),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text       = value,
                fontSize   = 28.sp,
                fontWeight = FontWeight.Bold,
                color      = color,
            )
            Text(
                text  = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }
    }
}

@Composable
private fun QuickAccessCard(
    title:   String,
    subtitle: String,
    icon:    ImageVector,
    color:   Color,
    onClick: () -> Unit,
) {
    Surface(
        shape    = MaterialTheme.shapes.large,
        color    = Surface,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier          = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier         = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = color,
                    modifier           = Modifier.size(24.dp),
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = title,
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = TextPrimary,
                )
                Text(
                    text  = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
            Icon(
                imageVector        = Icons.Default.ChevronRight,
                contentDescription = null,
                tint               = TextFaint,
            )
        }
    }
}