package com.rhapp.data.remote.api

import com.rhapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface DepartamentoApi {
    @GET("departamentos/")
    suspend fun getDepartamentos(): Response<PaginatedDto<DepartamentoDto>>

    @GET("departamentos/{id}/")
    suspend fun getDepartamento(@Path("id") id: Int): Response<DepartamentoDto>

    @POST("departamentos/")
    suspend fun createDepartamento(@Body body: DepartamentoRequestDto): Response<DepartamentoDto>

    @PATCH("departamentos/{id}/")
    suspend fun updateDepartamento(
        @Path("id") id: Int,
        @Body body: DepartamentoRequestDto,
    ): Response<DepartamentoDto>

    @DELETE("departamentos/{id}/")
    suspend fun deleteDepartamento(@Path("id") id: Int): Response<Unit>

    @GET("departamentos/activos/")
    suspend fun getActivos(): Response<ActivosDepartamentosDto>

    @GET("departamentos/{id}/empleados_total/")
    suspend fun getEmpleadosTotal(@Path("id") id: Int): Response<Map<String, Int>>

    @GET("departamentos/estadisticas/")
    suspend fun getStats(): Response<DepartamentoStatsDto>
}