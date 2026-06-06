package com.rhapp.domain.model

enum class EstadoNomina(val value: String, val label: String) {
    GENERADA("generada", "Generada"),
    REVISADA("revisada", "Revisada"),
    PAGADA("pagada", "Pagada"),
    ANULADA("anulada", "Anulada");

    companion object {
        fun fromValue(v: String) = entries.firstOrNull { it.value == v } ?: GENERADA
    }
}

data class Nomina(
    val id: Int,
    val empleadoId: Int,
    val empleadoNombre: String,
    val mes: Int,
    val anio: Int,
    val salarioBase: Double,
    val bono: Double,
    val descuentoAportes: Double,
    val descuentoImpuestos: Double,
    val salarioNeto: Double,
    val estado: EstadoNomina,
    val fechaGeneracion: String,
    val fechaPago: String?,
)

data class NominaPayload(
    val empleadoId: Int,
    val mes: Int,
    val anio: Int,
    val salarioBase: Double,
    val bono: Double,
    val descuentoAportes: Double,
    val descuentoImpuestos: Double,
)