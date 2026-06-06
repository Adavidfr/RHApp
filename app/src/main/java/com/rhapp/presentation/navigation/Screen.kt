package com.rhapp.presentation.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")

    // Dashboard principal
    data object Home : Screen("home")

    // Módulos admin
    data object AdminEmpleados     : Screen("admin/empleados")
    data object AdminDepartamentos : Screen("admin/departamentos")
    data object AdminPuestos       : Screen("admin/puestos")
    data object AdminNominas       : Screen("admin/nominas")
    data object AdminAsistencias   : Screen("admin/asistencias")

    // Detalle con parámetro
    data class EmpleadoDetalle(val id: Int = 0) : Screen("empleado/{id}") {
        fun createRoute(id: Int) = "empleado/$id"
    }
}