package com.rhapp.domain.model

data class Departamento(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val presupuestoAnual: Double,
    val jefeId: Int?,
    val jefeNombre: String?,
    val activo: Boolean,
    val totalEmpleados: Int,
)

data class DepartamentoPayload(
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val presupuestoAnual: Double,
    val jefeId: Int?,
    val activo: Boolean,
)