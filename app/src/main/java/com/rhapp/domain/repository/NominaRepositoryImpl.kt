package com.rhapp.data.repository

import com.rhapp.data.remote.api.NominaApi
import com.rhapp.data.remote.dto.toDomain
import com.rhapp.data.remote.dto.toRequest
import com.rhapp.domain.model.Nomina
import com.rhapp.domain.model.NominaPayload
import com.rhapp.domain.repository.NominaRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NominaRepositoryImpl @Inject constructor(
    private val api: NominaApi,
) : NominaRepository {

    override suspend fun getNominas(mes: Int?, anio: Int?): Result<List<Nomina>> = runCatching {
        val r = api.getNominas(mes, anio)
        if (r.isSuccessful) r.body()!!.results.map { it.toDomain() }
        else error("Error ${r.code()}")
    }

    override suspend fun getNomina(id: Int): Result<Nomina> = runCatching {
        val r = api.getNomina(id)
        if (r.isSuccessful) r.body()!!.toDomain()
        else error("Error ${r.code()}")
    }

    override suspend fun createNomina(payload: NominaPayload): Result<Nomina> = runCatching {
        val r = api.createNomina(payload.toRequest())
        if (r.isSuccessful) r.body()!!.toDomain()
        else error("Error ${r.code()}: ${r.errorBody()?.string()}")
    }

    override suspend fun marcarPagada(id: Int): Result<Nomina> = runCatching {
        val r = api.marcarPagada(id)
        if (r.isSuccessful) r.body()!!.toDomain()
        else error("Error ${r.code()}")
    }

    override suspend fun deleteNomina(id: Int): Result<Unit> = runCatching {
        val r = api.deleteNomina(id)
        if (!r.isSuccessful) error("Error ${r.code()}")
    }

    override suspend fun getStats(): Result<Map<String, Any>> = runCatching {
        val r = api.getEstadisticas()
        if (r.isSuccessful) {
            val s = r.body()!!
            mapOf<String, Any>(
                "total"      to ((s["total"] as? Int) ?: 0),
                "pendientes" to ((s["pendientes"] as? Int) ?: 0),
                "pagadas"    to ((s["pagadas"] as? Int) ?: 0),
                "generadas"  to ((s["generadas"] as? Int) ?: 0),
                "revisadas"  to ((s["revisadas"] as? Int) ?: 0),
                "anuladas"   to ((s["anuladas"] as? Int) ?: 0),
            )
        } else error("Error ${r.code()}")
    }
}