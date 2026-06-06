package com.rhapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhapp.domain.model.Nomina
import com.rhapp.domain.repository.NominaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface NominaDetailUiState {
    data object Loading                    : NominaDetailUiState
    data class  Success(val nomina: Nomina) : NominaDetailUiState
    data class  Error(val message: String) : NominaDetailUiState
}

@HiltViewModel
class NominaDetailViewModel @Inject constructor(
    private val repository: NominaRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<NominaDetailUiState>(NominaDetailUiState.Loading)
    val state: StateFlow<NominaDetailUiState> = _state.asStateFlow()

    fun load(id: Int) {
        viewModelScope.launch {
            _state.value = NominaDetailUiState.Loading
            repository.getNomina(id)
                .onSuccess { _state.value = NominaDetailUiState.Success(it) }
                .onFailure { _state.value = NominaDetailUiState.Error(it.message ?: "Error") }
        }
    }
}
