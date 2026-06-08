package com.rhapp.data.repository

import com.rhapp.data.local.TokenDataStore
import com.rhapp.data.remote.api.AuthApi
import com.rhapp.data.remote.dto.LoginRequest
import com.rhapp.data.remote.dto.LogoutRequest
import com.rhapp.domain.model.LoggedUser
import com.rhapp.domain.repository.AuthRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api:            AuthApi,
    private val tokenDataStore: TokenDataStore,
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<LoggedUser> =
        runCatching {
            val response = api.login(LoginRequest(username, password))
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string() ?: ""
                error(parseErrorMessage(errorBody, response.code()))
            }
            val body = response.body()!!
            // Guardar tokens primero (necesarios para el interceptor de auth)
            tokenDataStore.saveTokens(body.access, body.refresh)

            // Obtener perfil del usuario desde /auth/me/ (login solo devuelve tokens)
            val meResponse = api.me()
            val me = if (meResponse.isSuccessful) meResponse.body() else null

            val userId   = me?.id       ?: body.resolvedUserId()
            val uname    = me?.username ?: body.resolvedUsername()
            val email    = me?.email    ?: body.resolvedEmail()
            val isStaff  = me?.isStaff  ?: body.resolvedIsStaff()

            tokenDataStore.saveUser(userId, uname, email, isStaff)
            LoggedUser(userId, uname, email, isStaff)
        }

    override suspend fun logout(): Result<Unit> = runCatching {
        val refresh = tokenDataStore.getRefreshToken()
        if (refresh != null) {
            runCatching { api.logout(LogoutRequest(refresh)) }
        }
        tokenDataStore.clearSession()
    }

    override suspend fun getStoredUser(): TokenDataStore.UserSnapshot? =
        tokenDataStore.userSnapshot.first()

    override suspend fun isLoggedIn(): Boolean =
        !tokenDataStore.getAccessToken().isNullOrBlank()

    private fun parseErrorMessage(body: String, code: Int): String {
        return try {
            val map = Gson().fromJson(body, Map::class.java)
            map["detail"]?.toString()
                ?: map["non_field_errors"]?.toString()
                ?: map.values.firstOrNull()?.toString()
                ?: "Error $code"
        } catch (e: Exception) {
            "Error $code"
        }
    }
}