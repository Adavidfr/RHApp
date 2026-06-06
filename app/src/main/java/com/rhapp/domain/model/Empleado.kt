package com.rhapp.domain.model

enum class TipoContrato(val value: String, val label: String) {
    INDEFINIDO("indefinido", "Indefinido"),
    TEMPORAL("temporal", "Temporal"),
    PRACTICANTE("practicante", "Practicante"),
    CONSULTOR("consultor", "Consultor");

    companion object {
        fun fromValue(v: String) = entries.firstOrNull { it.value == v } ?: INDEFINIDO
    }
}

enum class EstadoEmpleado(val value: String, val label: String) {
    ACTIVO("activo", "Activo"),
    EN_LICENCIA("en_licencia", "En Licencia"),
    SUSPENDIDO("suspendido", "Suspendido"),
    INACTIVO("inactivo", "Inactivo");

    companion object {
        fun fromValue(v: String) = entries.firstOrNull { it.value == v } ?: ACTIVO
    }
}

data class Empleado(
    val id: Int,
    val numeroEmpleado: String,
    val cedula: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String,
    val direccion: String,
    val fechaNacimiento: String,
    val salarioActual: Double,
    val tipoContrato: TipoContrato,
    val estado: EstadoEmpleado,
    val puestoId: Int,
    val puestoTitulo: String,
    val departamentoId: Int,
    val departamentoNombre: String,
    val supervisorId: Int?,
    val supervisorNombre: String?,
)

data class EmpleadoPayload(
    val cedula: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String,
    val direccion: String,
    val fechaNacimiento: String,
    val salarioActual: Double,
    val tipoContrato: String,
    val estado: String,
    val puestoId: Int,
    val supervisorId: Int?,
)