package com.rhapp.domain.repository

import com.rhapp.domain.model.Nomina
import com.rhapp.domain.model.NominaPayload

interface NominaRepository {
    suspend fun getNominas(mes: Int? = null, anio: Int? = null): Result<List<Nomina>>
    suspend fun getNomina(id: Int): Result<Nomina>
    suspend fun createNomina(payload: NominaPayload): Result<Nomina>
    suspend fun marcarPagada(id: Int): Result<Nomina>
    suspend fun deleteNomina(id: Int): Result<Unit>
    suspend fun getStats(): Result<Map<String, Any>>
}