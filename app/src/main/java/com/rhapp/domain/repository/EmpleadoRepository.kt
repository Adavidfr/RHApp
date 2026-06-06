package com.rhapp.domain.repository

import com.rhapp.domain.model.Empleado
import com.rhapp.domain.model.EmpleadoPayload

interface EmpleadoRepository {
    suspend fun getEmpleados(filters: Map<String, String> = emptyMap()): Result<List<Empleado>>
    suspend fun getEmpleado(id: Int): Result<Empleado>
    suspend fun getActivos(): Result<List<Empleado>>
    suspend fun createEmpleado(payload: EmpleadoPayload): Result<Empleado>
    suspend fun updateEmpleado(id: Int, payload: EmpleadoPayload): Result<Empleado>
    suspend fun deleteEmpleado(id: Int): Result<Unit>
    suspend fun getStats(): Result<Map<String, Any>>
}