package com.rhapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhapp.domain.model.EstadoNomina
import com.rhapp.domain.model.Nomina
import com.rhapp.domain.repository.NominaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NominasUiState(
    val nominas:     List<Nomina> = emptyList(),
    val isLoading:   Boolean      = false,
    val error:       String?      = null,
    val statusFilter: String      = "",
)

@HiltViewModel
class NominasViewModel @Inject constructor(
    private val repository: NominaRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(NominasUiState())
    val state: StateFlow<NominasUiState> = _state.asStateFlow()

    init { load() }

    fun load(reset: Boolean = true) {
        if (reset) {
            _state.update { it.copy(isLoading = true, error = null) }
        }

        viewModelScope.launch {
            repository.getNominas(
                mes  = null,
                anio = null,
            ).onSuccess { nominas ->
                val filtered = if (_state.value.statusFilter.isBlank()) {
                    nominas
                } else {
                    nominas.filter { it.estado.value == _state.value.statusFilter }
                }
                _state.update { s ->
                    s.copy(
                        nominas   = filtered,
                        isLoading = false,
                        error     = null,
                    )
                }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun setStatusFilter(status: String) {
        _state.update { it.copy(statusFilter = status) }
        load(reset = true)
    }

    fun refresh() = load(reset = true)
}
