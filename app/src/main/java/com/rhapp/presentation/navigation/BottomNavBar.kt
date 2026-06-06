package com.rhapp.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rhapp.theme.*

data class BottomNavItem(
    val screen:       Screen,
    val label:        String,
    val icon:         ImageVector,
    val iconSelected: ImageVector,
)

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(Screen.Home,           "Inicio",       Icons.Outlined.Home,          Icons.Filled.Home),
        BottomNavItem(Screen.AdminEmpleados, "Empleados",    Icons.Outlined.People,         Icons.Filled.People),
        BottomNavItem(Screen.AdminNominas,   "Nóminas",      Icons.Outlined.Payments,       Icons.Filled.Payments),
        BottomNavItem(Screen.AdminAsistencias,"Asistencia",  Icons.Outlined.CalendarMonth,  Icons.Filled.CalendarMonth),
        BottomNavItem(Screen.AdminDepartamentos,"Depto.",    Icons.Outlined.Business,       Icons.Filled.Business),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute      = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Surface,
        tonalElevation = 0.dp,
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.screen.route
            NavigationBarItem(
                selected = isSelected,
                onClick  = {
                    navController.navigate(item.screen.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState    = true
                    }
                },
                icon  = {
                    Icon(
                        imageVector        = if (isSelected) item.iconSelected else item.icon,
                        contentDescription = item.label,
                    )
                },
                label  = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = Accent,
                    selectedTextColor   = Accent,
                    indicatorColor      = Accent.copy(alpha = 0.12f),
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                ),
            )
        }
    }
}