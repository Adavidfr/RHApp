package com.rhapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhapp.domain.model.Empleado
import com.rhapp.domain.model.EstadoNomina
import com.rhapp.domain.model.Nomina
import com.rhapp.domain.model.NominaPayload
import com.rhapp.domain.repository.EmpleadoRepository
import com.rhapp.domain.repository.NominaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

enum class NominaEstadoFilter(val label: String, val value: EstadoNomina?) {
    TODOS("Todos", null),
    GENERADA("Generada", EstadoNomina.GENERADA),
    REVISADA("Revisada", EstadoNomina.REVISADA),
    PAGADA("Pagada", EstadoNomina.PAGADA),
    ANULADA("Anulada", EstadoNomina.ANULADA),
}

data class NominasAdminUiState(
    val nominas:      List<Nomina>          = emptyList(),
    val empleados:    List<Empleado>        = emptyList(),
    val isLoading:    Boolean               = false,
    val error:        String?               = null,
    val estadoFilter: NominaEstadoFilter    = NominaEstadoFilter.TODOS,
    val mesFilter:    Int?                  = null,
    val anioFilter:   Int                   = Calendar.getInstance().get(Calendar.YEAR),
    val search:       String                = "",
)

sealed interface NominaFormState {
    data object Idle                       : NominaFormState
    data object Saving                     : NominaFormState
    data class  Success(val msg: String)   : NominaFormState
    data class  Error(val message: String) : NominaFormState
}

@HiltViewModel
class NominasAdminViewModel @Inject constructor(
    private val nominaRepository:   NominaRepository,
    private val empleadoRepository: EmpleadoRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(NominasAdminUiState())
    val state: StateFlow<NominasAdminUiState> = _state.asStateFlow()

    private val _formState = MutableStateFlow<NominaFormState>(NominaFormState.Idle)
    val formState: StateFlow<NominaFormState> = _formState.asStateFlow()

    val filtered: StateFlow<List<Nomina>> = _state
        .map { s ->
            s.nominas
                .filter { n ->
                    s.estadoFilter.value == null || n.estado == s.estadoFilter.value
                }
                .filter { n ->
                    s.mesFilter == null || n.mes == s.mesFilter
                }
                .filter { n ->
                    s.search.isBlank() ||
                    n.empleadoNombre.contains(s.search, ignoreCase = true) ||
                    n.mes.toString() == s.search
                }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val nominasResult   = nominaRepository.getNominas(anio = _state.value.anioFilter)
            val empleadosResult = empleadoRepository.getActivos()

            _state.update { s ->
                s.copy(
                    nominas   = nominasResult.getOrElse { s.nominas },
                    empleados = empleadosResult.getOrElse { s.empleados },
                    isLoading = false,
                    error     = if (nominasResult.isFailure)
                                    nominasResult.exceptionOrNull()?.message
                                else null,
                )
            }
        }
    }

    fun setSearch(q: String) = _state.update { it.copy(search = q) }
    fun setEstadoFilter(f: NominaEstadoFilter) = _state.update { it.copy(estadoFilter = f) }
    fun setMesFilter(mes: Int?) = _state.update { it.copy(mesFilter = mes) }
    fun setAnio(anio: Int) {
        _state.update { it.copy(anioFilter = anio) }
        load()
    }

    // Cambio de estado optimista (para el dropdown inline)
    fun marcarPagada(id: Int) {
        _state.update { s ->
            s.copy(nominas = s.nominas.map { if (it.id == id) it.copy(estado = EstadoNomina.PAGADA) else it })
        }
        viewModelScope.launch {
            nominaRepository.marcarPagada(id)
                .onFailure { e ->
                    // revertir
                    _state.update { s ->
                        s.copy(nominas = s.nominas.map { n ->
                            if (n.id == id) n.copy(estado = EstadoNomina.REVISADA) else n
                        })
                    }
                    _state.update { it.copy(error = e.message) }
                }
        }
    }

    fun createNomina(payload: NominaPayload) {
        _formState.value = NominaFormState.Saving
        viewModelScope.launch {
            nominaRepository.createNomina(payload)
                .onSuccess { created ->
                    _state.update { s -> s.copy(nominas = listOf(created) + s.nominas) }
                    _formState.value = NominaFormState.Success("Nómina generada")
                }
                .onFailure { e ->
                    _formState.value = NominaFormState.Error(e.message ?: "Error al generar nómina")
                }
        }
    }

    fun deleteNomina(id: Int, onResult: (String) -> Unit) {
        viewModelScope.launch {
            nominaRepository.deleteNomina(id)
                .onSuccess {
                    _state.update { s -> s.copy(nominas = s.nominas.filter { it.id != id }) }
                    onResult("Nómina eliminada")
                }
                .onFailure { e -> onResult("Error: ${e.message}") }
        }
    }

    fun resetFormState() { _formState.value = NominaFormState.Idle }
}
