package com.rhapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rhapp.domain.model.Empleado
import com.rhapp.domain.model.EmpleadoPayload
import com.rhapp.domain.model.EstadoEmpleado
import com.rhapp.domain.model.TipoContrato

data class EmpleadoDto(
    val id:                  Int,
    @SerializedName("numero_empleado")    val numeroEmpleado:    String?,
    val cedula:              String?,
    val nombre:              String,
    val apellido:            String,
    val email:               String?,
    val telefono:            String?,
    val direccion:           String?,
    @SerializedName("fecha_nacimiento")   val fechaNacimiento:   String?,
    @SerializedName("fecha_ingreso")      val fechaIngreso:      String?,
    @SerializedName("salario_actual")     val salarioActual:     String?,   // servidor envía "600.00" como String
    @SerializedName("tipo_contrato")      val tipoContrato:      String?,
    @SerializedName("status")            val estado:            String?,   // servidor envía "status", no "estado"
    @SerializedName("puesto")             val puestoId:          Int?,
    @SerializedName("puesto_titulo")      val puestoTitulo:      String?,
    @SerializedName("departamento")       val departamentoId:    Int?,
    @SerializedName("departamento_nombre") val departamentoNombre: String?,
    @SerializedName("supervisor")         val supervisorId:      Int?,
    @SerializedName("supervisor_nombre")  val supervisorNombre:  String?,
)

data class EmpleadoRequestDto(
    val cedula:              String,
    val nombre:              String,
    val apellido:            String,
    val email:               String,
    val telefono:            String,
    val direccion:           String,
    @SerializedName("numero_empleado") val numeroEmpleado:  String,
    @SerializedName("fecha_nacimiento") val fechaNacimiento: String,
    @SerializedName("fecha_ingreso")    val fechaIngreso:    String,
    val edad:                Int,
    @SerializedName("salario_actual")   val salarioActual:   Double,
    @SerializedName("tipo_contrato")    val tipoContrato:    String,
    @SerializedName("status")          val estado:          String,
    @SerializedName("puesto")           val puestoId:        Int,
    @SerializedName("departamento")     val departamentoId:  Int,
    @SerializedName("supervisor")       val supervisorId:    Int?,
)

fun EmpleadoDto.toDomain() = Empleado(
    id                 = id,
    numeroEmpleado     = numeroEmpleado     ?: "",
    cedula             = cedula             ?: "",
    nombre             = nombre,
    apellido           = apellido,
    email              = email              ?: "",
    telefono           = telefono           ?: "",
    direccion          = direccion          ?: "",
    fechaNacimiento    = fechaNacimiento    ?: "",
    salarioActual      = salarioActual?.toDoubleOrNull() ?: 0.0,
    tipoContrato       = TipoContrato.fromValue(tipoContrato ?: ""),
    estado             = EstadoEmpleado.fromValue(estado ?: "activo"),
    puestoId           = puestoId           ?: 0,
    puestoTitulo       = puestoTitulo       ?: "",
    departamentoId     = departamentoId     ?: 0,
    departamentoNombre = departamentoNombre ?: "",
    supervisorId       = supervisorId,
    supervisorNombre   = supervisorNombre,
)

fun EmpleadoPayload.toRequest() = EmpleadoRequestDto(
    cedula          = cedula,
    nombre          = nombre,
    apellido        = apellido,
    email           = email,
    telefono        = telefono,
    direccion       = direccion,
    numeroEmpleado  = numeroEmpleado,
    fechaNacimiento = fechaNacimiento,
    fechaIngreso    = fechaIngreso,
    edad            = edad,
    salarioActual   = salarioActual,
    tipoContrato    = tipoContrato,
    estado          = estado,
    puestoId        = puestoId,
    departamentoId  = departamentoId,
    supervisorId    = supervisorId,
)

data class EmpleadoStatsDto(
    val total:           Int,
    val activos:         Int,
    val inactivos:       Int,
    val porDepartamento: Map<String, Int>,
)

// El endpoint /empleados/activos/ devuelve {"value": [...], "Count": N}
data class ActivosEmpleadosDto(
    val value: List<EmpleadoDto>,
    @com.google.gson.annotations.SerializedName("Count") val count: Int,
)
