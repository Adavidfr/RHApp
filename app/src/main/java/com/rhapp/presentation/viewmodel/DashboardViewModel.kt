package com.rhapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhapp.domain.repository.AsistenciaRepository
import com.rhapp.domain.repository.DepartamentoRepository
import com.rhapp.domain.repository.EmpleadoRepository
import com.rhapp.domain.repository.NominaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class DashboardStats(
    val totalEmpleados:   Int = 0,
    val totalDepartamentos: Int = 0,
    val presentesHoy:     Int = 0,
    val nominasPendientes: Int = 0,
    val isLoading:        Boolean = true,
    val error:            String? = null,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val empleadoRepo:    EmpleadoRepository,
    private val departamentoRepo: DepartamentoRepository,
    private val asistenciaRepo:  AsistenciaRepository,
    private val nominaRepo:      NominaRepository,
) : ViewModel() {

    private val _stats = MutableStateFlow(DashboardStats())
    val stats: StateFlow<DashboardStats> = _stats.asStateFlow()

    init { loadStats() }

    fun loadStats() {
        viewModelScope.launch {
            _stats.update { it.copy(isLoading = true, error = null) }
            try {
                val cal  = Calendar.getInstance()
                val mes  = cal.get(Calendar.MONTH) + 1
                val anio = cal.get(Calendar.YEAR)

                val empleados    = empleadoRepo.getActivos().getOrNull() ?: emptyList()
                val departamentos = departamentoRepo.getActivos().getOrNull() ?: emptyList()
                val presentes    = asistenciaRepo.getPresentesHoy().getOrNull() ?: emptyList()
                val nominas      = nominaRepo.getNominas(mes, anio).getOrNull() ?: emptyList()
                val pendientes   = nominas.count { it.estado.value != "pagada" }

                _stats.value = DashboardStats(
                    totalEmpleados    = empleados.size,
                    totalDepartamentos = departamentos.size,
                    presentesHoy      = presentes.size,
                    nominasPendientes = pendientes,
                    isLoading         = false,
                )
            } catch (e: Exception) {
                _stats.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}