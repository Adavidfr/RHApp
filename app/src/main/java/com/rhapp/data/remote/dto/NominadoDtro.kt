package com.rhapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rhapp.domain.model.EstadoNomina
import com.rhapp.domain.model.Nomina
import com.rhapp.domain.model.NominaPayload

data class NominaDto(
    val id:                  Int,
    @SerializedName("empleado")          val empleadoId:        Int,
    @SerializedName("empleado_nombre")   val empleadoNombre:    String,
    val mes:                 Int,
    val anio:                Int,
    @SerializedName("salario_base")      val salarioBase:       Double,
    val bono:                Double,
    @SerializedName("descuento_aportes") val descuentoAportes:  Double,
    @SerializedName("descuento_impuestos") val descuentoImpuestos: Double,
    @SerializedName("salario_neto")      val salarioNeto:       Double,
    val estado:              String,
    @SerializedName("fecha_generacion")  val fechaGeneracion:   String,
    @SerializedName("fecha_pago")        val fechaPago:         String?,
)

data class NominaRequestDto(
    @SerializedName("empleado")            val empleadoId:         Int,
    val mes:                 Int,
    val anio:                Int,
    @SerializedName("salario_base")        val salarioBase:        Double,
    val bono:                Double,
    @SerializedName("descuento_aportes")   val descuentoAportes:   Double,
    @SerializedName("descuento_impuestos") val descuentoImpuestos: Double,
)

fun NominaDto.toDomain() = Nomina(
    id                  = id,
    empleadoId          = empleadoId,
    empleadoNombre      = empleadoNombre,
    mes                 = mes,
    anio                = anio,
    salarioBase         = salarioBase,
    bono                = bono,
    descuentoAportes    = descuentoAportes,
    descuentoImpuestos  = descuentoImpuestos,
    salarioNeto         = salarioNeto,
    estado              = EstadoNomina.fromValue(estado),
    fechaGeneracion     = fechaGeneracion,
    fechaPago           = fechaPago,
)

fun NominaPayload.toRequest() = NominaRequestDto(
    empleadoId          = empleadoId,
    mes                 = mes,
    anio                = anio,
    salarioBase         = salarioBase,
    bono                = bono,
    descuentoAportes    = descuentoAportes,
    descuentoImpuestos  = descuentoImpuestos,
)