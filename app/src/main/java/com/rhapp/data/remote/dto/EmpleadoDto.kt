package com.rhapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rhapp.domain.model.Empleado
import com.rhapp.domain.model.EmpleadoPayload
import com.rhapp.domain.model.EstadoEmpleado
import com.rhapp.domain.model.TipoContrato

data class EmpleadoDto(
    val id:                  Int,
    @SerializedName("numero_empleado")    val numeroEmpleado:   String,
    val cedula:              String,
    val nombre:              String,
    val apellido:            String,
    val email:               String,
    val telefono:            String,
    val direccion:           String,
    @SerializedName("fecha_nacimiento")   val fechaNacimiento:  String,
    @SerializedName("salario_actual")     val salarioActual:    Double,
    @SerializedName("tipo_contrato")      val tipoContrato:     String,
    val estado:              String,
    @SerializedName("puesto")             val puestoId:         Int,
    @SerializedName("puesto_titulo")      val puestoTitulo:     String,
    @SerializedName("departamento")       val departamentoId:   Int,
    @SerializedName("departamento_nombre") val departamentoNombre: String,
    @SerializedName("supervisor")         val supervisorId:     Int?,
    @SerializedName("supervisor_nombre")  val supervisorNombre: String?,
)

data class EmpleadoRequestDto(
    val cedula:              String,
    val nombre:              String,
    val apellido:            String,
    val email:               String,
    val telefono:            String,
    val direccion:           String,
    @SerializedName("fecha_nacimiento") val fechaNacimiento: String,
    @SerializedName("salario_actual")   val salarioActual:   Double,
    @SerializedName("tipo_contrato")    val tipoContrato:    String,
    val estado:              String,
    @SerializedName("puesto")           val puestoId:        Int,
    @SerializedName("supervisor")       val supervisorId:    Int?,
)

fun EmpleadoDto.toDomain() = Empleado(
    id                  = id,
    numeroEmpleado      = numeroEmpleado,
    cedula              = cedula,
    nombre              = nombre,
    apellido            = apellido,
    email               = email,
    telefono            = telefono,
    direccion           = direccion,
    fechaNacimiento     = fechaNacimiento,
    salarioActual       = salarioActual,
    tipoContrato        = TipoContrato.fromValue(tipoContrato),
    estado              = EstadoEmpleado.fromValue(estado),
    puestoId            = puestoId,
    puestoTitulo        = puestoTitulo,
    departamentoId      = departamentoId,
    departamentoNombre  = departamentoNombre,
    supervisorId        = supervisorId,
    supervisorNombre    = supervisorNombre,
)

fun EmpleadoPayload.toRequest() = EmpleadoRequestDto(
    cedula          = cedula,
    nombre          = nombre,
    apellido        = apellido,
    email           = email,
    telefono        = telefono,
    direccion       = direccion,
    fechaNacimiento = fechaNacimiento,
    salarioActual   = salarioActual,
    tipoContrato    = tipoContrato,
    estado          = estado,
    puestoId        = puestoId,
    supervisorId    = supervisorId,
)