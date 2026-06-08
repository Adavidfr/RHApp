package com.rhapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rhapp.domain.model.Puesto
import com.rhapp.domain.model.PuestoPayload

data class PuestoDto(
    val id:           Int,
    @SerializedName("code")               val codigo:             String,
    val titulo:       String,
    val descripcion:  String?,
    val requisitos:   String?,
    @SerializedName("salario_base")       val salarioBase:        String,   // Servidor devuelve "500.00" como String
    @SerializedName("salario_maximo")     val salarioMaximo:      String,
    @SerializedName("departamento")       val departamentoId:     Int,
    @SerializedName("departamento_nombre") val departamentoNombre: String?,
    @SerializedName("is_active")          val activo:             Boolean,
    @SerializedName("empleados_count")    val empleadosCount:     Int = 0,
)

data class PuestoRequestDto(
    @SerializedName("code")           val codigo:        String,
    val titulo:       String,
    val descripcion:  String?,
    val requisitos:   String?,
    @SerializedName("salario_base")   val salarioBase:   Double,
    @SerializedName("salario_maximo") val salarioMaximo: Double,
    @SerializedName("departamento")   val departamentoId: Int,
    @SerializedName("is_active")      val activo:        Boolean,
)

fun PuestoDto.toDomain() = Puesto(
    id                 = id,
    codigo             = codigo,
    titulo             = titulo,
    descripcion        = descripcion ?: "",
    requisitos         = requisitos ?: "",
    salarioBase        = salarioBase.toDoubleOrNull() ?: 0.0,
    salarioMaximo      = salarioMaximo.toDoubleOrNull() ?: 0.0,
    departamentoId     = departamentoId,
    departamentoNombre = departamentoNombre ?: "",
    activo             = activo,
)

fun PuestoPayload.toRequest() = PuestoRequestDto(
    codigo         = codigo,
    titulo         = titulo,
    descripcion    = descripcion,
    requisitos     = requisitos,
    salarioBase    = salarioBase,
    salarioMaximo  = salarioMaximo,
    departamentoId = departamentoId,
    activo         = activo,
)

data class PuestoStatsDto(
    val total:      Int,
    val activos:    Int,
    val inactivos:  Int,
)