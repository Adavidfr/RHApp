package com.rhapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import com.rhapp.presentation.components.LoadingScreen
import com.rhapp.presentation.navigation.Screen
import com.rhapp.presentation.ui.auth.LoginScreen
import com.rhapp.presentation.viewmodel.AuthViewModel
import com.rhapp.theme.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RHAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RHApp()
                }
            }
        }
    }
}

@Composable
fun RHApp() {
    val authViewModel: AuthViewModel = hiltViewModel()

    val isCheckingSession by authViewModel.isCheckingSession.collectAsState()
    val isAuthenticated   by authViewModel.isAuthenticated.collectAsState()
    val isStaff           by authViewModel.isStaff.collectAsState()

    val navController = rememberNavController()

    if (isCheckingSession) {
        LoadingScreen(message = "Iniciando RH App...")
        return
    }

    val startDestination = when {
        !isAuthenticated -> Screen.Login.route
        isStaff          -> Screen.AdminDashboard.route
        else             -> Screen.Home.route
    }

    NavHost(navController = navController, startDestination = startDestination) {

        // ── LOGIN ───────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { isStaffUser ->
                    val dest = if (isStaffUser) Screen.AdminDashboard.route else Screen.Home.route
                    navController.navigate(dest) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
            )
        }

        // ── HOME EMPLEADO ───────────────────────────────────
        composable(Screen.Home.route) {
            HomeEmpleadoScreen(
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        // ── ADMIN DASHBOARD ─────────────────────────────────
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
    }
}

// ── Placeholder Home Empleado ────────────────────────────────
@Composable
fun HomeEmpleadoScreen(onLogout: () -> Unit) {
    Box(
        modifier         = Modifier.fillMaxSize().background(Background),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text       = "RH App",
                fontSize   = 28.sp,
                fontWeight = FontWeight.Bold,
                color      = Accent,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text  = "Portal del Empleado",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = "Navegación completa en Módulo 4",
                color = TextFaint,
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onLogout,
                colors  = ButtonDefaults.buttonColors(containerColor = Accent),
            ) {
                Text("Cerrar sesión", color = AccentOnDark)
            }
        }
    }
}

// ── Placeholder Admin Dashboard ──────────────────────────────
@Composable
fun AdminDashboardScreen(onLogout: () -> Unit) {
    Box(
        modifier         = Modifier.fillMaxSize().background(Background),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text       = "RH App",
                fontSize   = 28.sp,
                fontWeight = FontWeight.Bold,
                color      = Accent,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text  = "Panel de Administración",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = "Módulos 7-11 completarán esto",
                color = TextFaint,
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onLogout,
                colors  = ButtonDefaults.buttonColors(containerColor = Accent),
            ) {
                Text("Cerrar sesión", color = AccentOnDark)
            }
        }
    }
}