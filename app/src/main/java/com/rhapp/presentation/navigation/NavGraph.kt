package com.rhapp.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.rhapp.presentation.components.LoadingScreen
import com.rhapp.presentation.ui.auth.LoginScreen
import com.rhapp.presentation.ui.dashboard.DashboardScreen
import com.rhapp.presentation.ui.nominas.NominasScreen
import com.rhapp.presentation.ui.nominas.NominaDetailScreen
import com.rhapp.presentation.ui.profile.ProfileScreen
import com.rhapp.presentation.viewmodel.AuthViewModel
import com.rhapp.theme.Surface

@Composable
fun NavGraph(authViewModel: AuthViewModel) {
    val isCheckingSession by authViewModel.isCheckingSession.collectAsState()

    if (isCheckingSession) {
        LoadingScreen("Iniciando RH App...")
        return
    }

    NavGraphContent(authViewModel = authViewModel)
}

@Composable
private fun NavGraphContent(authViewModel: AuthViewModel) {
    val navController   = rememberNavController()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute      = navBackStackEntry?.destination?.route

    // Rutas donde se muestra el BottomNavBar
    val routesConBottomBar = listOf(
        Screen.Home.route,
        Screen.AdminEmpleados.route,
        Screen.AdminNominas.route,
        Screen.AdminAsistencias.route,
        Screen.AdminDepartamentos.route,
        Screen.Nominas.route,
        Screen.Profile.route,
    )
    val showBottomBar = currentRoute in routesConBottomBar

    val startDestination = remember {
        if (isAuthenticated) Screen.Home.route else Screen.Login.route
    }

    LaunchedEffect(isAuthenticated) {
        if (!isAuthenticated) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        containerColor = Surface,
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController)
            }
        },
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = startDestination,
            modifier         = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {

            // ── LOGIN ────────────────────────────────────────
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { _ ->
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    viewModel = authViewModel,
                )
            }

            // ── DASHBOARD / HOME ─────────────────────────────
            composable(Screen.Home.route) {
                DashboardScreen(
                    onNavigate    = { route -> navController.navigate(route) },
                    onLogout      = { authViewModel.logout() },
                    authViewModel = authViewModel,
                )
            }

            // ── EMPLEADOS (placeholder M5) ───────────────────
            composable(Screen.AdminEmpleados.route) {
                PlaceholderScreen(titulo = "Gestión de Empleados", modulo = "Módulo 5")
            }

            // ── DEPARTAMENTOS (placeholder M6) ───────────────
            composable(Screen.AdminDepartamentos.route) {
                PlaceholderScreen(titulo = "Departamentos & Puestos", modulo = "Módulo 6")
            }

            // ── NÓMINAS (placeholder M7) ─────────────────────
            composable(Screen.AdminNominas.route) {
                PlaceholderScreen(titulo = "Control de Nóminas", modulo = "Módulo 7")
            }

            // ── ASISTENCIAS (placeholder M8) ─────────────────
            composable(Screen.AdminAsistencias.route) {
                PlaceholderScreen(titulo = "Registro de Asistencia", modulo = "Módulo 8")
            }

            // ── DETALLE EMPLEADO (placeholder M5) ────────────
            composable("empleado/{id}") {
                PlaceholderScreen(titulo = "Detalle de Empleado", modulo = "Módulo 5")
            }

            // ── NÓMINAS (empleado) ───────────────────────────
            composable(Screen.Nominas.route) {
                NominasScreen(
                    onNominaClick = { id -> navController.navigate(Screen.NominaDetalle().createRoute(id)) },
                )
            }

            // ── DETALLE NÓMINA ────────────────────────────────
            composable(
                route     = Screen.NominaDetalle().route,
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: return@composable
                NominaDetailScreen(
                    nominaId = id,
                    onBack    = { navController.popBackStack() },
                )
            }

            // ── PERFIL ────────────────────────────────────────
            composable(Screen.Profile.route) {
                ProfileScreen(
                    authViewModel = authViewModel,
                    onLogout      = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(titulo: String, modulo: String) {
    LoadingScreen(message = "$titulo — $modulo")
}