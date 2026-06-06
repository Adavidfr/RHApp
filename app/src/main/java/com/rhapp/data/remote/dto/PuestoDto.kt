package com.rhapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rhapp.domain.model.Puesto
import com.rhapp.domain.model.PuestoPayload

data class PuestoDto(
    val id:           Int,
    val codigo:       String,
    val titulo:       String,
    val descripcion:  String,
    val requisitos:   String,
    @SerializedName("salario_base")    val salarioBase:    Double,
    @SerializedName("salario_maximo")  val salarioMaximo:  Double,
    @SerializedName("departamento")    val departamentoId: Int,
    @SerializedName("departamento_nombre") val departamentoNombre: String,
    val activo:       Boolean,
)

data class PuestoRequestDto(
    val codigo:       String,
    val titulo:       String,
    val descripcion:  String,
    val requisitos:   String,
    @SerializedName("salario_base")   val salarioBase:    Double,
    @SerializedName("salario_maximo") val salarioMaximo:  Double,
    @SerializedName("departamento")   val departamentoId: Int,
    val activo:       Boolean,
)

fun PuestoDto.toDomain() = Puesto(
    id                  = id,
    codigo              = codigo,
    titulo              = titulo,
    descripcion         = descripcion,
    requisitos          = requisitos,
    salarioBase         = salarioBase,
    salarioMaximo       = salarioMaximo,
    departamentoId      = departamentoId,
    departamentoNombre  = departamentoNombre,
    activo              = activo,
)

fun PuestoPayload.toRequest() = PuestoRequestDto(
    codigo          = codigo,
    titulo          = titulo,
    descripcion     = descripcion,
    requisitos      = requisitos,
    salarioBase     = salarioBase,
    salarioMaximo   = salarioMaximo,
    departamentoId  = departamentoId,
    activo          = activo,
)

data class PuestoStatsDto(
    val total:      Int,
    val activos:    Int,
    val inactivos:  Int,
)