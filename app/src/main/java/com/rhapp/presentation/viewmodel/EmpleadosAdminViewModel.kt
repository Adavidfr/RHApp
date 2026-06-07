package com.rhapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhapp.domain.model.Empleado
import com.rhapp.domain.model.EmpleadoPayload
import com.rhapp.domain.model.EstadoEmpleado
import com.rhapp.domain.model.Puesto
import com.rhapp.domain.model.TipoContrato
import com.rhapp.domain.repository.EmpleadoRepository
import com.rhapp.domain.repository.PuestoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── Filtros ────────────────────────────────────────────────────

enum class EmpleadoEstadoFilter(val label: String, val value: EstadoEmpleado?) {
    TODOS("Todos", null),
    ACTIVO("Activo", EstadoEmpleado.ACTIVO),
    EN_LICENCIA("En Licencia", EstadoEmpleado.EN_LICENCIA),
    SUSPENDIDO("Suspendido", EstadoEmpleado.SUSPENDIDO),
    INACTIVO("Inactivo", EstadoEmpleado.INACTIVO),
}

enum class EmpleadoContratoFilter(val label: String, val value: TipoContrato?) {
    TODOS("Todos", null),
    INDEFINIDO("Indefinido", TipoContrato.INDEFINIDO),
    TEMPORAL("Temporal", TipoContrato.TEMPORAL),
    PRACTICANTE("Practicante", TipoContrato.PRACTICANTE),
    CONSULTOR("Consultor", TipoContrato.CONSULTOR),
}

// ── UI State ───────────────────────────────────────────────────

data class EmpleadosAdminUiState(
    val empleados:      List<Empleado>           = emptyList(),
    val puestos:        List<Puesto>             = emptyList(),
    val isLoading:      Boolean                  = false,
    val error:          String?                  = null,
    val search:         String                   = "",
    val estadoFilter:   EmpleadoEstadoFilter     = EmpleadoEstadoFilter.TODOS,
    val contratoFilter: EmpleadoContratoFilter   = EmpleadoContratoFilter.TODOS,
)

sealed interface EmpleadoFormState {
    data object Idle                       : EmpleadoFormState
    data object Saving                     : EmpleadoFormState
    data class  Success(val msg: String)   : EmpleadoFormState
    data class  Error(val message: String) : EmpleadoFormState
}

// ── ViewModel ──────────────────────────────────────────────────

@HiltViewModel
class EmpleadosAdminViewModel @Inject constructor(
    private val empleadoRepository: EmpleadoRepository,
    private val puestoRepository:   PuestoRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(EmpleadosAdminUiState())
    val state: StateFlow<EmpleadosAdminUiState> = _state.asStateFlow()

    private val _formState = MutableStateFlow<EmpleadoFormState>(EmpleadoFormState.Idle)
    val formState: StateFlow<EmpleadoFormState> = _formState.asStateFlow()

    // Filtrado combinado: búsqueda + estado + tipo contrato
    val filtered: StateFlow<List<Empleado>> = _state
        .map { s ->
            s.empleados
                .filter { e ->
                    s.search.isBlank() ||
                    e.nombre.contains(s.search, ignoreCase = true) ||
                    e.apellido.contains(s.search, ignoreCase = true) ||
                    e.cedula.contains(s.search, ignoreCase = true) ||
                    e.numeroEmpleado.contains(s.search, ignoreCase = true)
                }
                .filter { e ->
                    s.estadoFilter.value == null || e.estado == s.estadoFilter.value
                }
                .filter { e ->
                    s.contratoFilter.value == null || e.tipoContrato == s.contratoFilter.value
                }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val empleadosResult = empleadoRepository.getEmpleados()
            val puestosResult   = puestoRepository.getActivos()

            if (empleadosResult.isSuccess) {
                _state.update { s ->
                    s.copy(
                        empleados  = empleadosResult.getOrThrow(),
                        puestos    = puestosResult.getOrElse { emptyList() },
                        isLoading  = false,
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false, error = empleadosResult.exceptionOrNull()?.message) }
            }
        }
    }

    fun setSearch(query: String) = _state.update { it.copy(search = query) }
    fun setEstadoFilter(f: EmpleadoEstadoFilter) = _state.update { it.copy(estadoFilter = f) }
    fun setContratoFilter(f: EmpleadoContratoFilter) = _state.update { it.copy(contratoFilter = f) }

    fun createEmpleado(payload: EmpleadoPayload) {
        _formState.value = EmpleadoFormState.Saving
        viewModelScope.launch {
            empleadoRepository.createEmpleado(payload)
                .onSuccess { created ->
                    _state.update { s -> s.copy(empleados = listOf(created) + s.empleados) }
                    _formState.value = EmpleadoFormState.Success("Empleado creado")
                }
                .onFailure { e ->
                    _formState.value = EmpleadoFormState.Error(e.message ?: "Error al crear")
                }
        }
    }

    fun updateEmpleado(id: Int, payload: EmpleadoPayload) {
        _formState.value = EmpleadoFormState.Saving
        viewModelScope.launch {
            empleadoRepository.updateEmpleado(id, payload)
                .onSuccess { updated ->
                    _state.update { s ->
                        s.copy(empleados = s.empleados.map { if (it.id == id) updated else it })
                    }
                    _formState.value = EmpleadoFormState.Success("Empleado actualizado")
                }
                .onFailure { e ->
                    _formState.value = EmpleadoFormState.Error(e.message ?: "Error al actualizar")
                }
        }
    }

    fun deleteEmpleado(id: Int, onResult: (String) -> Unit) {
        viewModelScope.launch {
            empleadoRepository.deleteEmpleado(id)
                .onSuccess {
                    _state.update { s -> s.copy(empleados = s.empleados.filter { it.id != id }) }
                    onResult("Empleado eliminado")
                }
                .onFailure { e ->
                    onResult("Error: ${e.message}")
                }
        }
    }

    fun resetFormState() { _formState.value = EmpleadoFormState.Idle }
}
