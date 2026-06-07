package com.rhapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhapp.domain.model.Asistencia
import com.rhapp.domain.model.AsistenciaPayload
import com.rhapp.domain.model.Empleado
import com.rhapp.domain.model.EstadoAsistencia
import com.rhapp.domain.repository.AsistenciaRepository
import com.rhapp.domain.repository.EmpleadoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

enum class AsistenciaEstadoFilter(val label: String, val value: EstadoAsistencia?) {
    TODOS("Todos", null),
    PRESENTE("Presente", EstadoAsistencia.PRESENTE),
    AUSENTE("Ausente", EstadoAsistencia.AUSENTE),
    LICENCIA("Licencia", EstadoAsistencia.LICENCIA),
    RETARDO("Retardo", EstadoAsistencia.RETARDO),
    SALIDA_ANT("Salida ant.", EstadoAsistencia.SALIDA_ANTICIPADA),
}

data class AsistenciasAdminUiState(
    val asistencias:   List<Asistencia>          = emptyList(),
    val empleados:     List<Empleado>            = emptyList(),
    val isLoading:     Boolean                   = false,
    val error:         String?                   = null,
    val estadoFilter:  AsistenciaEstadoFilter    = AsistenciaEstadoFilter.TODOS,
    val fechaFilter:   String                    = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
    val search:        String                    = "",
)

sealed interface AsistenciaFormState {
    data object Idle                       : AsistenciaFormState
    data object Saving                     : AsistenciaFormState
    data class  Success(val msg: String)   : AsistenciaFormState
    data class  Error(val message: String) : AsistenciaFormState
}

@HiltViewModel
class AsistenciasAdminViewModel @Inject constructor(
    private val asistenciaRepository: AsistenciaRepository,
    private val empleadoRepository:   EmpleadoRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AsistenciasAdminUiState())
    val state: StateFlow<AsistenciasAdminUiState> = _state.asStateFlow()

    private val _formState = MutableStateFlow<AsistenciaFormState>(AsistenciaFormState.Idle)
    val formState: StateFlow<AsistenciaFormState> = _formState.asStateFlow()

    val filtered: StateFlow<List<Asistencia>> = _state
        .map { s ->
            s.asistencias
                .filter { a -> s.estadoFilter.value == null || a.status == s.estadoFilter.value }
                .filter { a ->
                    s.search.isBlank() || a.empleadoNombre.contains(s.search, ignoreCase = true)
                }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val asistResult   = asistenciaRepository.getAsistencias(fecha = _state.value.fechaFilter)
            val empResult     = empleadoRepository.getActivos()

            if (asistResult.isSuccess) {
                _state.update { s ->
                    s.copy(
                        asistencias = asistResult.getOrThrow(),
                        empleados   = empResult.getOrElse { emptyList() },
                        isLoading   = false,
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false, error = asistResult.exceptionOrNull()?.message) }
            }
        }
    }

    fun setSearch(q: String) = _state.update { it.copy(search = q) }
    fun setEstadoFilter(f: AsistenciaEstadoFilter) = _state.update { it.copy(estadoFilter = f) }
    fun setFecha(fecha: String) {
        _state.update { it.copy(fechaFilter = fecha) }
        load()
    }

    fun registrarEntrada(payload: AsistenciaPayload) {
        _formState.value = AsistenciaFormState.Saving
        viewModelScope.launch {
            asistenciaRepository.registrarAsistencia(payload)
                .onSuccess { nueva ->
                    _state.update { s -> s.copy(asistencias = listOf(nueva) + s.asistencias) }
                    _formState.value = AsistenciaFormState.Success("Asistencia registrada")
                }
                .onFailure { e ->
                    _formState.value = AsistenciaFormState.Error(e.message ?: "Error al registrar")
                }
        }
    }

    fun registrarSalida(id: Int, onResult: (String) -> Unit) {
        viewModelScope.launch {
            asistenciaRepository.registrarSalida(id)
                .onSuccess { updated ->
                    _state.update { s ->
                        s.copy(asistencias = s.asistencias.map { if (it.id == id) updated else it })
                    }
                    onResult("Salida registrada")
                }
                .onFailure { e -> onResult("Error: ${e.message}") }
        }
    }

    fun deleteAsistencia(id: Int, onResult: (String) -> Unit) {
        viewModelScope.launch {
            _state.update { s -> s.copy(asistencias = s.asistencias.filter { it.id != id }) }
            onResult("Registro eliminado")
        }
    }

    fun resetFormState() { _formState.value = AsistenciaFormState.Idle }
}
