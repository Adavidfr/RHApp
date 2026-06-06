package com.rhapp.data.remote.api

import com.rhapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface EmpleadoApi {
    @GET("empleados/")
    suspend fun getEmpleados(
        @QueryMap filters: Map<String, String> = emptyMap(),
    ): Response<PaginatedDto<EmpleadoDto>>

    @GET("empleados/{id}/")
    suspend fun getEmpleado(@Path("id") id: Int): Response<EmpleadoDto>

    @GET("empleados/activos/")
    suspend fun getActivos(): Response<List<EmpleadoDto>>

    @POST("empleados/")
    suspend fun createEmpleado(@Body body: EmpleadoRequestDto): Response<EmpleadoDto>

    @PATCH("empleados/{id}/")
    suspend fun updateEmpleado(
        @Path("id") id: Int,
        @Body body: EmpleadoRequestDto,
    ): Response<EmpleadoDto>

    @DELETE("empleados/{id}/")
    suspend fun deleteEmpleado(@Path("id") id: Int): Response<Unit>

    @GET("empleados/{id}/historial_nominas/")
    suspend fun getHistorialNominas(@Path("id") id: Int): Response<List<NominaDto>>

    @GET("empleados/{id}/historial_asistencia/")
    suspend fun getHistorialAsistencia(@Path("id") id: Int): Response<List<AsistenciaDto>>

    @GET("empleados/{id}/subordinados/")
    suspend fun getSubordinados(@Path("id") id: Int): Response<List<EmpleadoDto>>

    @GET("empleados/estadisticas/")
    suspend fun getStats(): Response<EmpleadoStatsDto>
}