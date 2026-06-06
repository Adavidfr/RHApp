package com.rhapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rhapp.domain.model.Departamento
import com.rhapp.domain.model.DepartamentoPayload

data class DepartamentoDto(
    val id:          Int,
    val codigo:      String,
    val nombre:      String,
    val descripcion: String,
    @SerializedName("presupuesto_anual") val presupuestoAnual: Double,
    @SerializedName("jefe")             val jefeId:           Int?,
    @SerializedName("jefe_nombre")      val jefeNombre:       String?,
    val activo:      Boolean,
    @SerializedName("total_empleados")  val totalEmpleados:   Int,
)

data class DepartamentoRequestDto(
    val codigo:      String,
    val nombre:      String,
    val descripcion: String,
    @SerializedName("presupuesto_anual") val presupuestoAnual: Double,
    @SerializedName("jefe")             val jefeId:           Int?,
    val activo:      Boolean,
)

fun DepartamentoDto.toDomain() = Departamento(
    id               = id,
    codigo           = codigo,
    nombre           = nombre,
    descripcion      = descripcion,
    presupuestoAnual = presupuestoAnual,
    jefeId           = jefeId,
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