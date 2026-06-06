package com.rhapp.data.remote.api

import com.rhapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface PuestoApi {
    @GET("puestos/")
    suspend fun getPuestos(): Response<PaginatedDto<PuestoDto>>

    @GET("puestos/{id}/")
    suspend fun getPuesto(@Path("id") id: Int): Response<PuestoDto>

    @POST("puestos/")
    suspend fun createPuesto(@Body body: PuestoRequestDto): Response<PuestoDto>

    @PATCH("puestos/{id}/")
    suspend fun updatePuesto(
        @Path("id") id: Int,
        @Body body: PuestoRequestDto,
    ): Response<PuestoDto>

    @DELETE("puestos/{id}/")
    suspend fun deletePuesto(@Path("id") id: Int): Response<Unit>

    @GET("puestos/activos/")
    suspend fun getActivos(): Response<List<PuestoDto>>

    @GET("puestos/{id}/empleados_puesto/")
    suspend fun getEmpleadosPuesto(@Path("id") id: Int): Response<List<EmpleadoDto>>
}