package com.rhapp.domain.repository

import com.rhapp.domain.model.Puesto
import com.rhapp.domain.model.PuestoPayload

interface PuestoRepository {
    suspend fun getPuestos(): Result<List<Puesto>>
    suspend fun getPuesto(id: Int): Result<Puesto>
    suspend fun createPuesto(payload: PuestoPayload): Result<Puesto>
    suspend fun updatePuesto(id: Int, payload: PuestoPayload): Result<Puesto>
    suspend fun deletePuesto(id: Int): Result<Unit>
    suspend fun getActivos(): Result<List<Puesto>>
    suspend fun getStats(): Result<Map<String, Any>>
}
