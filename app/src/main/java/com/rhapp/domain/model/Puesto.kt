package com.rhapp.domain.model

data class Puesto(
    val id: Int,
    val codigo: String,
    val titulo: String,
    val descripcion: String,
    val requisitos: String,
    val salarioBase: Double,
    val salarioMaximo: Double,
    val departamentoId: Int,
    val departamentoNombre: String,
    val activo: Boolean,
)

data class PuestoPayload(
    val codigo: String,
    val titulo: String,
    val descripcion: String,
    val requisitos: String,
    val salarioBase: Double,
    val salarioMaximo: Double,
    val departamentoId: Int,
    val activo: Boolean,
)