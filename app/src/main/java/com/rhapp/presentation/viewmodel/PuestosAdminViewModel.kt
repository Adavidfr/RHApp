package com.rhapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhapp.domain.model.Departamento
import com.rhapp.domain.model.Puesto
import com.rhapp.domain.model.PuestoPayload
import com.rhapp.domain.repository.DepartamentoRepository
import com.rhapp.domain.repository.PuestoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PuestosAdminUiState(
    val puestos:        List<Puesto>       = emptyList(),
    val departamentos:  List<Departamento> = emptyList(),
    val isLoading:      Boolean            = false,
    val error:          String?            = null,
    val search:         String             = "",
)

sealed interface PuestoFormState {
    data object Idle                      : PuestoFormState
    data object Saving                    : PuestoFormState
    data class  Success(val msg: String)  : PuestoFormState
    data class  Error(val message: String): PuestoFormState
}

@HiltViewModel
class PuestosAdminViewModel @Inject constructor(
    private val puestoRepository:      PuestoRepository,
    private val departamentoRepository: DepartamentoRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(PuestosAdminUiState())
    val state: StateFlow<PuestosAdminUiState> = _state.asStateFlow()

    private val _formState = MutableStateFlow<PuestoFormState>(PuestoFormState.Idle)
    val formState: StateFlow<PuestoFormState> = _formState.asStateFlow()

    val filtered: StateFlow<List<Puesto>> = _state
        .map { s ->
            if (s.search.isBlank()) s.puestos
            else s.puestos.filter {
                it.titulo.contains(s.search, ignoreCase = true) ||
                it.codigo.contains(s.search, ignoreCase = true) ||
                it.departamentoNombre.contains(s.search, ignoreCase = true)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Cargar puestos y departamentos activos en paralelo
            val puestosResult = puestoRepository.getPuestos()
            val depResult     = departamentoRepository.getActivos()

            if (puestosResult.isSuccess) {
                _state.update { s ->
                    s.copy(
                        puestos        = puestosResult.getOrThrow(),
                        departamentos  = depResult.getOrElse { emptyList() },
                        isLoading      = false,
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false, error = puestosResult.exceptionOrNull()?.message) }
            }
        }
    }

    fun setSearch(query: String) {
        _state.update { it.copy(search = query) }
    }

    // Toggle optimista
    fun toggleActive(id: Int, isActive: Boolean) {
        _state.update { s ->
            s.copy(puestos = s.puestos.map {
                if (it.id == id) it.copy(activo = isActive) else it
            })
        }
        viewModelScope.launch {
            val puesto = _state.value.puestos.first { it.id == id }
            puestoRepository.updatePuesto(
                id,
                PuestoPayload(
                    codigo         = puesto.codigo,
                    titulo         = puesto.titulo,
                    descripcion    = puesto.descripcion,
                    requisitos     = puesto.requisitos,
                    salarioBase    = puesto.salarioBase,
                    salarioMaximo  = puesto.salarioMaximo,
                    departamentoId = puesto.departamentoId,
                    activo         = isActive,
                )
            ).onFailure {
                // Revertir
                _state.update { s ->
                    s.copy(puestos = s.puestos.map { p ->
                        if (p.id == id) p.copy(activo = !isActive) else p
                    })
                }
            }
        }
    }

    fun createPuesto(payload: PuestoPayload) {
        _formState.value = PuestoFormState.Saving
        viewModelScope.launch {
            puestoRepository.createPuesto(payload)
                .onSuccess { created ->
                    _state.update { s ->
                        s.copy(puestos = listOf(created) + s.puestos)
                    }
                    _formState.value = PuestoFormState.Success("Puesto creado")
                }
                .onFailure { e ->
                    _formState.value = PuestoFormState.Error(e.message ?: "Error al crear")
                }
        }
    }

    fun updatePuesto(id: Int, payload: PuestoPayload) {
        _formState.value = PuestoFormState.Saving
        viewModelScope.launch {
            puestoRepository.updatePuesto(id, payload)
                .onSuccess { updated ->
                    _state.update { s ->
                        s.copy(puestos = s.puestos.map {
                            if (it.id == id) updated else it
                        })
                    }
                    _formState.value = PuestoFormState.Success("Puesto actualizado")
                }
                .onFailure { e ->
                    _formState.value = PuestoFormState.Error(e.message ?: "Error al actualizar")
                }
        }
    }

    fun deletePuesto(id: Int) {
        viewModelScope.launch {
            puestoRepository.deletePuesto(id)
                .onSuccess {
                    _state.update { s ->
                        s.copy(puestos = s.puestos.filter { it.id != id })
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(error = e.message) }
                }
        }
    }

    fun resetFormState() { _formState.value = PuestoFormState.Idle }
}
