package com.rhapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.rhapp.domain.model.Asistencia
import com.rhapp.domain.model.AsistenciaPayload
import com.rhapp.domain.model.EstadoAsistencia

data class AsistenciaDto(
    val id:                  Int,
    @SerializedName("empleado")         val empleadoId:      Int,
    @SerializedName("empleado_nombre")  val empleadoNombre:  String,
    val fecha:               String,
    val status:              String,
    @SerializedName("hora_entrada")     val horaEntrada:     String?,
    @SerializedName("hora_salida")      val horaSalida:      String?,
    @SerializedName("minutos_retardo")  val minutosRetardo:  Int,
    @SerializedName("horas_trabajadas") val horasTrabajadas: Double,
    val observaciones:       String,
)

data class AsistenciaRequestDto(
    @SerializedName("empleado")      val empleadoId:  Int,
    val fecha:           String,
    val status:          String,
    @SerializedName("hora_entrada")  val horaEntrada: String?,
    val observaciones:   String,
)

fun AsistenciaDto.toDomain() = Asistencia(
    id               = id,
    empleadoId       = empleadoId,
    empleadoNombre   = empleadoNombre,
    fecha            = fecha,
    status           = EstadoAsistencia.fromValue(status),
    horaEntrada      = horaEntrada,
    horaSalida       = horaSalida,
    minutosRetardo   = minutosRetardo,
    horasTrabajadas  = horasTrabajadas,
    observaciones    = observaciones,
)

fun AsistenciaPayload.toRequest() = AsistenciaRequestDto(
    empleadoId   = empleadoId,
    fecha        = fecha,
    status       = status,
    horaEntrada  = horaEntrada,
    observaciones = observaciones,
)

data class AsistenciaStatsDto(
    val total:      Int,
    val presentes:  Int,
    val ausentes:   Int,
    val licencias:  Int,
    val retardos:   Int,
)