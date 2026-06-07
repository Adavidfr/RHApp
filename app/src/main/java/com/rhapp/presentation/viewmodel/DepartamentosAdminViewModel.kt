package com.rhapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhapp.domain.model.Departamento
import com.rhapp.domain.model.DepartamentoPayload
import com.rhapp.domain.repository.DepartamentoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DepartamentosAdminUiState(
    val departamentos: List<Departamento> = emptyList(),
    val isLoading:     Boolean            = false,
    val error:         String?            = null,
    val search:        String             = "",
)

sealed interface DepartamentoFormState {
    data object Idle                      : DepartamentoFormState
    data object Saving                    : DepartamentoFormState
    data class  Success(val msg: String)  : DepartamentoFormState
    data class  Error(val message: String): DepartamentoFormState
}

@HiltViewModel
class DepartamentosAdminViewModel @Inject constructor(
    private val repository: DepartamentoRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(DepartamentosAdminUiState())
    val state: StateFlow<DepartamentosAdminUiState> = _state.asStateFlow()

    private val _formState = MutableStateFlow<DepartamentoFormState>(DepartamentoFormState.Idle)
    val formState: StateFlow<DepartamentoFormState> = _formState.asStateFlow()

    val filtered: StateFlow<List<Departamento>> = _state
        .map { s ->
            if (s.search.isBlank()) s.departamentos
            else s.departamentos.filter {
                it.nombre.contains(s.search, ignoreCase = true) ||
                it.codigo.contains(s.search, ignoreCase = true)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repository.getDepartamentos()
                .onSuccess { list ->
                    _state.update { it.copy(departamentos = list, isLoading = false) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun setSearch(query: String) {
        _state.update { it.copy(search = query) }
    }

    // Toggle optimista: actualiza UI inmediatamente y revierte si falla
    fun toggleActive(id: Int, isActive: Boolean) {
        _state.update { s ->
            s.copy(departamentos = s.departamentos.map {
                if (it.id == id) it.copy(activo = isActive) else it
            })
        }
        viewModelScope.launch {
            val dep = _state.value.departamentos.first { it.id == id }
            repository.updateDepartamento(
                id,
                DepartamentoPayload(
                    codigo           = dep.codigo,
                    nombre           = dep.nombre,
                    descripcion      = dep.descripcion,
                    presupuestoAnual = dep.presupuestoAnual,
                    jefeId           = dep.jefeId,
                    activo           = isActive,
                )
            ).onFailure {
                // Revertir al estado anterior
                _state.update { s ->
                    s.copy(departamentos = s.departamentos.map { d ->
                        if (d.id == id) d.copy(activo = !isActive) else d
                    })
                }
            }
        }
    }

    fun createDepartamento(payload: DepartamentoPayload) {
        _formState.value = DepartamentoFormState.Saving
        viewModelScope.launch {
            repository.createDepartamento(payload)
                .onSuccess { created ->
                    _state.update { s ->
                        s.copy(departamentos = listOf(created) + s.departamentos)
                    }
                    _formState.value = DepartamentoFormState.Success("Departamento creado")
                }
                .onFailure { e ->
                    _formState.value = DepartamentoFormState.Error(e.message ?: "Error al crear")
                }
        }
    }

    fun updateDepartamento(id: Int, payload: DepartamentoPayload) {
        _formState.value = DepartamentoFormState.Saving
        viewModelScope.launch {
            repository.updateDepartamento(id, payload)
                .onSuccess { updated ->
                    _state.update { s ->
                        s.copy(departamentos = s.departamentos.map {
                            if (it.id == id) updated else it
                        })
                    }
                    _formState.value = DepartamentoFormState.Success("Departamento actualizado")
                }
                .onFailure { e ->
                    _formState.value = DepartamentoFormState.Error(e.message ?: "Error al actualizar")
                }
        }
    }

    fun deleteDepartamento(id: Int) {
        viewModelScope.launch {
            repository.deleteDepartamento(id)
                .onSuccess {
                    _state.update { s ->
                        s.copy(departamentos = s.departamentos.filter { it.id != id })
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(error = e.message) }
                }
        }
    }

    fun resetFormState() { _formState.value = DepartamentoFormState.Idle }
}
