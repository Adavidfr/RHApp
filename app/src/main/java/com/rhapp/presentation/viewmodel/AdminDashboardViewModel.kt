package com.rhapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhapp.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminDashboardStats(
    val totalEmpleados:      Int    = 0,
    val empleadosActivos:    Int    = 0,
    val empleadosInactivos:  Int    = 0,
    val totalDepartamentos:  Int    = 0,
    val departamentosActivos:Int    = 0,
    val totalPuestos:        Int    = 0,
    val puestosActivos:      Int    = 0,
    val totalNominas:        Int    = 0,
    val nominasPendientes:   Int    = 0,
    val nominasPagadas:      Int    = 0,
    val totalAsistencias:    Int    = 0,
    val presentesHoy:        Int    = 0,
    val ausentesHoy:         Int    = 0,
    val retardosHoy:         Int    = 0,
    val empleadosPorDepto:  Map<String, Int> = emptyMap(),
    val nominasPorEstado:    Map<String, Int> = emptyMap(),
)

sealed interface AdminDashboardUiState {
    data object Loading                                : AdminDashboardUiState
    data class  Success(val stats: AdminDashboardStats) : AdminDashboardUiState
    data class  Error(val message: String)             : AdminDashboardUiState
}

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val departamentoRepository: DepartamentoRepository,
    private val puestoRepository:      PuestoRepository,
    private val nominaRepository:     NominaRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<AdminDashboardUiState>(AdminDashboardUiState.Loading)
    val state: StateFlow<AdminDashboardUiState> = _state.asStateFlow()

    private val _lastUpdated = MutableStateFlow<Long>(0L)
    val lastUpdated: StateFlow<Long> = _lastUpdated.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = AdminDashboardUiState.Loading

            try {
                // Llamadas en paralelo usando endpoints que SÍ existen en el backend
                val deptosDeferred      = async { departamentoRepository.getDepartamentos() }
                val deptosActivosDeferred = async { departamentoRepository.getActivos() }
                val puestosDeferred     = async { puestoRepository.getPuestos() }
                val puestosActivosDeferred = async { puestoRepository.getActivos() }
                val nominaStatsDeferred = async { nominaRepository.getStats() }

                val deptos        = deptosDeferred.await().getOrDefault(emptyList())
                val deptosActivos = deptosActivosDeferred.await().getOrDefault(emptyList())
                val puestos       = puestosDeferred.await().getOrDefault(emptyList())
                val puestosActivos = puestosActivosDeferred.await().getOrDefault(emptyList())
                val nominaStats   = nominaStatsDeferred.await().getOrDefault(emptyMap())

                val stats = AdminDashboardStats(
                    totalEmpleados       = 0,
                    empleadosActivos     = 0,
                    empleadosInactivos   = 0,
                    totalDepartamentos   = deptos.size,
                    departamentosActivos = deptosActivos.size,
                    totalPuestos         = puestos.size,
                    puestosActivos       = puestosActivos.size,
                    totalNominas         = (nominaStats["total"]     as? Int) ?: 0,
                    nominasPendientes    = (nominaStats["pendientes"] as? Int) ?: 0,
                    nominasPagadas       = (nominaStats["pagadas"]    as? Int) ?: 0,
                    totalAsistencias     = 0,
                    presentesHoy         = 0,
                    ausentesHoy          = 0,
                    retardosHoy          = 0,
                    empleadosPorDepto    = emptyMap(),
                    nominasPorEstado     = mapOf(
                        "generada" to ((nominaStats["generadas"] as? Int) ?: 0),
                        "revisada" to ((nominaStats["revisadas"] as? Int) ?: 0),
                        "pagada"   to ((nominaStats["pagadas"]   as? Int) ?: 0),
                        "anulada"  to ((nominaStats["anuladas"]  as? Int) ?: 0),
                    ),
                )

                _state.value       = AdminDashboardUiState.Success(stats)
                _lastUpdated.value = System.currentTimeMillis()

            } catch (e: Exception) {
                _state.value = AdminDashboardUiState.Error(e.message ?: "Error al cargar el dashboard")
            }
        }
    }
}
