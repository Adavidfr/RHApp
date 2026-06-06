package com.rhapp.data.repository

import com.rhapp.data.remote.api.DepartamentoApi
import com.rhapp.data.remote.dto.toDomain
import com.rhapp.data.remote.dto.toRequest
import com.rhapp.domain.model.Departamento
import com.rhapp.domain.model.DepartamentoPayload
import com.rhapp.domain.repository.DepartamentoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DepartamentoRepositoryImpl @Inject constructor(
    private val api: DepartamentoApi,
) : DepartamentoRepository {

    override suspend fun getDepartamentos(): Result<List<Departamento>> = runCatching {
        val r = api.getDepartamentos()
        if (r.isSuccessful) r.body()!!.results.map { it.toDomain() }
        else error("Error ${r.code()}: ${r.errorBody()?.string()}")
    }

    override suspend fun getDepartamento(id: Int): Result<Departamento> = runCatching {
        val r = api.getDepartamento(id)
        if (r.isSuccessful) r.body()!!.toDomain()
        else error("Error ${r.code()}")
    }

    override suspend fun createDepartamento(payload: DepartamentoPayload): Result<Departamento> = runCatching {
        val r = api.createDepartamento(payload.toRequest())
        if (r.isSuccessful) r.body()!!.toDomain()
        else error("Error ${r.code()}: ${r.errorBody()?.string()}")
    }

    override suspend fun updateDepartamento(id: Int, payload: DepartamentoPayload): Result<Departamento> = runCatching {
        val r = api.updateDepartamento(id, payload.toRequest())
        if (r.isSuccessful) r.body()!!.toDomain()
        else error("Error ${r.code()}: ${r.errorBody()?.string()}")
    }

    override suspend fun deleteDepartamento(id: Int): Result<Unit> = runCatching {
        val r = api.deleteDepartamento(id)
        if (!r.isSuccessful) error("Error ${r.code()}")
    }

    override suspend fun getActivos(): Result<List<Departamento>> = runCatching {
        val r = api.getActivos()
        if (r.isSuccessful) r.body()!!.map { it.toDomain() }
        else error("Error ${r.code()}")
    }

    override suspend fun getStats(): Result<Map<String, Any>> = runCatching {
        val r = api.getStats()
        if (r.isSuccessful) {
            val s = r.body()!!
            mapOf(
                "total"    to s.total,
                "activos"  to s.activos,
                "inactivos" to s.inactivos,
            )
        } else error("Error ${r.code()}")
    }
}