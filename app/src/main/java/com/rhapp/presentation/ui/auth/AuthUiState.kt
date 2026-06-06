package com.rhapp.presentation.ui.auth

import com.rhapp.domain.model.LoggedUser

sealed interface AuthUiState {
    data object Idle    : AuthUiState
    data object Loading : AuthUiState
    data class  Success(val user: LoggedUser) : AuthUiState
    data class  Error(val message: String)    : AuthUiState
}