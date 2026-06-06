package com.rhapp.domain.repository

import com.rhapp.data.local.TokenDataStore
import com.rhapp.domain.model.LoggedUser

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<LoggedUser>
    suspend fun logout(): Result<Unit>
    suspend fun getStoredUser(): TokenDataStore.UserSnapshot?
    suspend fun isLoggedIn(): Boolean
}