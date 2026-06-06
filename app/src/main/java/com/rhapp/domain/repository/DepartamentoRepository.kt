package com.rhapp.domain.repository

import com.rhapp.domain.model.Departamento
import com.rhapp.domain.model.DepartamentoPayload

interface DepartamentoRepository {
    suspend fun getDepartamentos(): Result<List<Departamento>>
    suspend fun getDepartamento(id: Int): Result<Departamento>
    suspend fun createDepartamento(payload: DepartamentoPayload): Result<Departamento>
    suspend fun updateDepartamento(id: Int, payload: DepartamentoPayload): Result<Departamento>
    suspend fun deleteDepartamento(id: Int): Result<Unit>
    suspend fun getActivos(): Result<List<Departamento>>
    suspend fun getStats(): Result<Map<String, Any>>
}