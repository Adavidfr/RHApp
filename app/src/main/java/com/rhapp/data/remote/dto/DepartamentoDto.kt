package com.rhapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rhapp.domain.model.Departamento
import com.rhapp.domain.model.DepartamentoPayload

data class DepartamentoDto(
    val id:          Int,
    @SerializedName("code")            val codigo:           String,
    val nombre:      String,
    val descripcion: String?,
    @SerializedName("presupuesto_anual") val presupuestoAnual: String?,  // servidor envía "50000.00" como String
    @SerializedName("jefe_departamento") val jefeNombre:       String?,
    @SerializedName("is_active")       val activo:           Boolean,
    @SerializedName("empleados_count") val totalEmpleados:   Int,
)

data class DepartamentoRequestDto(
    @SerializedName("code")            val codigo:           String,
    val nombre:      String,
    val descripcion: String?,
    @SerializedName("presupuesto_anual") val presupuestoAnual: Double?,   // en el request enviamos número
    @SerializedName("jefe_departamento") val jefeId:           Int?,
    @SerializedName("is_active")       val activo:           Boolean,
)

fun DepartamentoDto.toDomain() = Departamento(
    id               = id,
    codigo           = codigo,
    nombre           = nombre,
    descripcion      = descripcion ?: "",
    presupuestoAnual = presupuestoAnual?.toDoubleOrNull() ?: 0.0,
    jefeId           = null,        // servidor devuelve el nombre, no el ID
    jefeNombre       = jefeNombre,
    activo           = activo,
    totalEmpleados   = totalEmpleados,
)

fun DepartamentoPayload.toRequest() = DepartamentoRequestDto(
    codigo           = codigo,
    nombre           = nombre,
    descripcion      = descripcion,
    presupuestoAnual = presupuestoAnual,
    jefeId           = jefeId,
    activo           = activo,
)

data class DepartamentoStatsDto(
    val total:      Int,
    val activos:    Int,
    val inactivos:  Int,
)

// El endpoint /activos/ devuelve {"value": [...], "Count": N} — no una lista directa
data class ActivosDepartamentosDto(
    val value: List<DepartamentoDto>,
    @com.google.gson.annotations.SerializedName("Count") val count: Int,
)