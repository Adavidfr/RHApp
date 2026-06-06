package com.rhapp.data.repository

import com.rhapp.data.remote.api.PuestoApi
import com.rhapp.data.remote.dto.toDomain
import com.rhapp.data.remote.dto.toRequest
import com.rhapp.domain.model.Puesto
import com.rhapp.domain.model.PuestoPayload
import com.rhapp.domain.repository.PuestoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PuestoRepositoryImpl @Inject constructor(
    private val api: PuestoApi,
) : PuestoRepository {

    override suspend fun getPuestos(): Result<List<Puesto>> = runCatching {
        val r = api.getPuestos()
        if (r.isSuccessful) r.body()!!.results.map { it.toDomain() }
        else error("Error ${r.code()}: ${r.errorBody()?.string()}")
    }

    override suspend fun getPuesto(id: Int): Result<Puesto> = runCatching {
        val r = api.getPuesto(id)
        if (r.isSuccessful) r.body()!!.toDomain()
        else error("Error ${r.code()}")
    }

    override suspend fun createPuesto(payload: PuestoPayload): Result<Puesto> = runCatching {
        val r = api.createPuesto(payload.toRequest())
        if (r.isSuccessful) r.body()!!.toDomain()
        else error("Error ${r.code()}: ${r.errorBody()?.string()}")
    }

    override suspend fun updatePuesto(id: Int, payload: PuestoPayload): Result<Puesto> =
        runCatching {
            val r = api.updatePuesto(id, payload.toRequest())
            if (r.isSuccessful) r.body()!!.toDomain()
            else error("Error ${r.code()}: ${r.errorBody()?.string()}")
        }

    override suspend fun deletePuesto(id: Int): Result<Unit> = runCatching {
        val r = api.deletePuesto(id)
        if (!r.isSuccessful) error("Error ${r.code()}")
    }

    override suspend fun getActivos(): Result<List<Puesto>> = runCatching {
        val r = api.getActivos()
        if (r.isSuccessful) r.body()!!.map { it.toDomain() }
        else error("Error ${r.code()}")
    }

    override suspend fun getStats(): Result<Map<String, Any>> = runCatching {
        val r = api.getStats()
        if (r.isSuccessful) {
            val s = r.body()!!
            mapOf<String, Any>(
                "total"      to s.total,
                "activos"    to s.activos,
                "inactivos"  to s.inactivos,
            )
        } else error("Error ${r.code()}")
    }
}
