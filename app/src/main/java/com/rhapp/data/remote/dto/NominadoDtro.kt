package com.rhapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rhapp.domain.model.EstadoNomina
import com.rhapp.domain.model.Nomina
import com.rhapp.domain.model.NominaPayload

data class NominaDto(
    val id:                  Int,
    @SerializedName("empleado")                   val empleadoId:          Int,
    @SerializedName("empleado_nombre")            val empleadoNombre:      String?,
    val mes:                 Int,
    @SerializedName("año")                        val anio:                Int,       // servidor envía "año" con ñ
    @SerializedName("salario_base")               val salarioBase:         String?,   // String "600.00"
    val bono:                String?,                                                 // String "50.00"
    @SerializedName("descuento_aporte_empleado")  val descuentoAportes:    String?,   // nombre diferente al DTO original
    @SerializedName("descuento_impuestos")        val descuentoImpuestos:  String?,
    @SerializedName("salario_neto")               val salarioNeto:         String?,
    @SerializedName("status")                     val estado:              String?,   // "status" no "estado"
    @SerializedName("fecha_generacion")           val fechaGeneracion:     String?,
    @SerializedName("fecha_pago")                 val fechaPago:           String?,
)

data class NominaRequestDto(
    @SerializedName("empleado")            val empleadoId:         Int,
    val mes:                 Int,
    @SerializedName("año")                 val anio:               Int,
    @SerializedName("salario_base")        val salarioBase:        Double,
    val bono:                Double,
    @SerializedName("descuento_aportes")   val descuentoAportes:   Double,
    @SerializedName("descuento_impuestos") val descuentoImpuestos: Double,
)

fun NominaDto.toDomain() = Nomina(
    id                 = id,
    empleadoId         = empleadoId,
    empleadoNombre     = empleadoNombre     ?: "",
    mes                = mes,
    anio               = anio,
    salarioBase        = salarioBase?.toDoubleOrNull()        ?: 0.0,
    bono               = bono?.toDoubleOrNull()               ?: 0.0,
    descuentoAportes   = descuentoAportes?.toDoubleOrNull()   ?: 0.0,
    descuentoImpuestos = descuentoImpuestos?.toDoubleOrNull() ?: 0.0,
    salarioNeto        = salarioNeto?.toDoubleOrNull()        ?: 0.0,
    estado             = EstadoNomina.fromValue(estado ?: "generada"),
    fechaGeneracion    = fechaGeneracion    ?: "",
    fechaPago          = fechaPago,
)

fun NominaPayload.toRequest() = NominaRequestDto(
    empleadoId        = empleadoId,
    mes               = mes,
    anio              = anio,
    salarioBase       = salarioBase,
    bono              = bono,
    descuentoAportes  = descuentoAportes,
    descuentoImpuestos = descuentoImpuestos,
)