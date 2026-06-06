package com.rhapp.domain.repository

import com.rhapp.domain.model.Asistencia
import com.rhapp.domain.model.AsistenciaPayload

interface AsistenciaRepository {
    suspend fun getAsistencias(fecha: String? = null): Result<List<Asistencia>>
    suspend fun getPresentesHoy(): Result<List<Asistencia>>
    suspend fun registrarAsistencia(payload: AsistenciaPayload): Result<Asistencia>
    suspend fun registrarSalida(id: Int): Result<Asistencia>
    suspend fun getResumenMes(mes: Int, anio: Int): Result<Map<String, Any>>
    suspend fun getStats(): Result<Map<String, Any>>
}