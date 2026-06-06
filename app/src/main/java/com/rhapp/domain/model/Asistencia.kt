package com.rhapp.domain.model

enum class EstadoAsistencia(val value: String, val label: String) {
    PRESENTE("presente", "Presente"),
    AUSENTE("ausente", "Ausente"),
    LICENCIA("licencia", "Licencia"),
    RETARDO("retardo", "Retardo"),
    SALIDA_ANTICIPADA("salida_anticipada", "Salida anticipada");

    companion object {
        fun fromValue(v: String) = entries.firstOrNull { it.value == v } ?: PRESENTE
    }
}

data class Asistencia(
    val id: Int,
    val empleadoId: Int,
    val empleadoNombre: String,
    val fecha: String,
    val status: EstadoAsistencia,
    val horaEntrada: String?,
    val horaSalida: String?,
    val minutosRetardo: Int,
    val horasTrabajadas: Double,
    val observaciones: String,
)

data class AsistenciaPayload(
    val empleadoId: Int,
    val fecha: String,
    val status: String,
    val horaEntrada: String?,
    val observaciones: String,
)