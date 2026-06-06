package com.rhapp.data.repository

import com.rhapp.data.remote.api.AsistenciaApi
import com.rhapp.data.remote.dto.toDomain
import com.rhapp.data.remote.dto.toRequest
import com.rhapp.domain.model.Asistencia
import com.rhapp.domain.model.AsistenciaPayload
import com.rhapp.domain.repository.AsistenciaRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AsistenciaRepositoryImpl @Inject constructor(
    private val api: AsistenciaApi,
) : AsistenciaRepository {

    override suspend fun getAsistencias(fecha: String?): Result<List<Asistencia>> = runCatching {
        val r = api.getAsistencias(fecha)
        if (r.isSuccessful) r.body()!!.results.map { it.toDomain() }
        else error("Error ${r.code()}")
    }

    override suspend fun getPresentesHoy(): Result<List<Asistencia>> = runCatching {
        val r = api.getPresentesHoy()
        if (r.isSuccessful) r.body()!!.map { it.toDomain() }
        else error("Error ${r.code()}")
    }

    override suspend fun registrarAsistencia(payload: AsistenciaPayload): Result<Asistencia> =
        runCatching {
            val r = api.registrarAsistencia(payload.toRequest())
            if (r.isSuccessful) r.body()!!.toDomain()
            else error("Error ${r.code()}: ${r.errorBody()?.string()}")
        }

    override suspend fun registrarSalida(id: Int): Result<Asistencia> = runCatching {
        val r = api.registrarSalida(id)
        if (r.isSuccessful) r.body()!!.toDomain()
        else error("Error ${r.code()}")
    }

    override suspend fun getResumenMes(mes: Int, anio: Int): Result<Map<String, Any>> =
        runCatching {
            val r = api.getResumenMes(mes, anio)
            if (r.isSuccessful) r.body()!! as Map<String, Any>
            else error("Error ${r.code()}")
        }

    override suspend fun getStats(): Result<Map<String, Any>> = runCatching {
        val r = api.getStats()
        if (r.isSuccessful) {
            val s = r.body()!!
            mapOf(
                "total"      to s.total,
                "presentes"  to s.presentes,
                "ausentes"   to s.ausentes,
                "licencias"  to s.licencias,
                "retardos"   to s.retardos,
            )
        } else error("Error ${r.code()}")
    }
}