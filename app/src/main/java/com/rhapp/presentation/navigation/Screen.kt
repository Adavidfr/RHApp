package com.rhapp.presentation.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")

    // Dashboard principal
    data object Home : Screen("home")

    // Admin dashboard
    data object AdminDashboard : Screen("admin")

    // Módulos admin
    data object AdminEmpleados     : Screen("admin/empleados")
    data object AdminDepartamentos : Screen("admin/departamentos")
    data object AdminPuestos       : Screen("admin/puestos")
    data object AdminNominas       : Screen("admin/nominas")
    data object AdminAsistencias   : Screen("admin/asistencias")

    // Módulos empleado
    data object Nominas : Screen("nominas")
    data object Profile : Screen("profile")

    // Detalles con parámetro
    data class EmpleadoDetalle(val id: Int = 0) : Screen("empleado/{id}") {
        fun createRoute(id: Int) = "empleado/$id"
    }

    data class NominaDetalle(val id: Int = 0) : Screen("nomina/{id}") {
        fun createRoute(id: Int) = "nomina/$id"
    }
}