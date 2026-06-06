package com.rhapp.data.remote.api

import com.rhapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface NominaApi {
    @GET("nominas/")
    suspend fun getNominas(
        @Query("mes")  mes:  Int? = null,
        @Query("anio") anio: Int? = null,
    ): Response<PaginatedDto<NominaDto>>

    @GET("nominas/{id}/")
    suspend fun getNomina(@Path("id") id: Int): Response<NominaDto>

    @POST("nominas/")
    suspend fun createNomina(@Body body: NominaRequestDto): Response<NominaDto>

    @PATCH("nominas/{id}/")
    suspend fun updateNomina(
        @Path("id") id: Int,
        @Body body: NominaRequestDto,
    ): Response<NominaDto>

    @DELETE("nominas/{id}/")
    suspend fun deleteNomina(@Path("id") id: Int): Response<Unit>

    @PATCH("nominas/{id}/marcar_pagada/")
    suspend fun marcarPagada(@Path("id") id: Int): Response<NominaDto>

    @GET("nominas/estadisticas/")
    suspend fun getEstadisticas(): Response<Map<String, Any>>

    @GET("nominas/pagadas/")
    suspend fun getPagadas(): Response<List<NominaDto>>
}