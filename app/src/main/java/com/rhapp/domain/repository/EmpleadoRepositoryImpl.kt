package com.rhapp.data.repository

import com.rhapp.data.remote.api.EmpleadoApi
import com.rhapp.data.remote.dto.toDomain
import com.rhapp.data.remote.dto.toRequest
import com.rhapp.domain.model.Empleado
import com.rhapp.domain.model.EmpleadoPayload
import com.rhapp.domain.repository.EmpleadoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmpleadoRepositoryImpl @Inject constructor(
    private val api: EmpleadoApi,
) : EmpleadoRepository {

    override suspend fun getEmpleados(filters: Map<String, String>): Result<List<Empleado>> =
        runCatching {
            val r = api.getEmpleados(filters)
            if (r.isSuccessful) r.body()!!.results.map { it.toDomain() }
            else error("Error ${r.code()}: ${r.errorBody()?.string()}")
        }

    override suspend fun getEmpleado(id: Int): Result<Empleado> = runCatching {
        val r = api.getEmpleado(id)
        if (r.isSuccessful) r.body()!!.toDomain()
        else error("Error ${r.code()}")
    }

    override suspend fun getActivos(): Result<List<Empleado>> = runCatching {
        val r = api.getActivos()
        if (r.isSuccessful) r.body()!!.value.map { it.toDomain() }   // desenvuelve {"value":[...]}
        else error("Error ${r.code()}")
    }

    override suspend fun createEmpleado(payload: EmpleadoPayload): Result<Empleado> = runCatching {
        val r = api.createEmpleado(payload.toRequest())
        if (r.isSuccessful) r.body()!!.toDomain()
        else error("Error ${r.code()}: ${r.errorBody()?.string()}")
    }

    override suspend fun updateEmpleado(id: Int, payload: EmpleadoPayload): Result<Empleado> =
        runCatching {
            val r = api.updateEmpleado(id, payload.toRequest())
            if (r.isSuccessful) r.body()!!.toDomain()
            else error("Error ${r.code()}: ${r.errorBody()?.string()}")
        }

    override suspend fun deleteEmpleado(id: Int): Result<Unit> = runCatching {
        val r = api.deleteEmpleado(id)
        if (!r.isSuccessful) error("Error ${r.code()}")
    }

    override suspend fun getStats(): Result<Map<String, Any>> = runCatching {
        val r = api.getStats()
        if (r.isSuccessful) {
            val s = r.body()!!
            mapOf(
                "total"     to s.total,
                "activos"   to s.activos,
                "inactivos" to s.inactivos,
                "por_departamento" to s.porDepartamento,
            )
        } else error("Error ${r.code()}")
    }
}