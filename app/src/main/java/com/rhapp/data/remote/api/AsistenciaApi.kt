package com.rhapp.data.remote.api

import com.rhapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface AsistenciaApi {
    @GET("asistencias/")
    suspend fun getAsistencias(
        @Query("fecha") fecha: String? = null,
    ): Response<PaginatedDto<AsistenciaDto>>

    @GET("asistencias/{id}/")
    suspend fun getAsistencia(@Path("id") id: Int): Response<AsistenciaDto>

    @POST("asistencias/")
    suspend fun registrarAsistencia(@Body body: AsistenciaRequestDto): Response<AsistenciaDto>

    @PATCH("asistencias/{id}/")
    suspend fun updateAsistencia(
        @Path("id") id: Int,
        @Body body: AsistenciaRequestDto,
    ): Response<AsistenciaDto>

    @DELETE("asistencias/{id}/")
    suspend fun deleteAsistencia(@Path("id") id: Int): Response<Unit>

    @PATCH("asistencias/{id}/registrar_salida/")
    suspend fun registrarSalida(@Path("id") id: Int): Response<AsistenciaDto>

    @GET("asistencias/presentes_hoy/")
    suspend fun getPresentesHoy(): Response<List<AsistenciaDto>>

    @GET("asistencias/resumen_mes/")
    suspend fun getResumenMes(
        @Query("mes")  mes:  Int,
        @Query("anio") anio: Int,
    ): Response<Map<String, Any>>

    @GET("asistencias/estadisticas/")
    suspend fun getStats(): Response<AsistenciaStatsDto>
}