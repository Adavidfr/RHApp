package com.rhapp.presentation.navigation

sealed class Screen(val route: String) {
    // Auth
    data object Login : Screen("login")

    // Empleado (usuario normal)
    data object Home            : Screen("home")
    data object MiPerfil        : Screen("mi_perfil")
    data object MisNominas      : Screen("mis_nominas")
    data object MisAsistencias  : Screen("mis_asistencias")

    // Admin
    data object AdminDashboard  : Screen("admin")
    data object AdminEmpleados  : Screen("admin/empleados")
    data object AdminDepartamentos : Screen("admin/departamentos")
    data object AdminPuestos    : Screen("admin/puestos")
    data object AdminNominas    : Screen("admin/nominas")
    data object AdminAsistencias: Screen("admin/asistencias")

    // Detalle con parámetro
    data class EmpleadoDetalle(val id: Int = 0) : Screen("empleado/{id}") {
        fun createRoute(id: Int) = "empleado/$id"
    }
}