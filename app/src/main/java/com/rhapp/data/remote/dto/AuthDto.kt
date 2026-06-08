package com.rhapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val username: String,
    val password: String,
)

data class TokenRefreshRequest(
    val refresh: String,
)

data class LogoutRequest(
    val refresh: String,
)

data class UserDto(
    @SerializedName("id")       val userId:   Int?,
    val username: String?,
    val email:    String?,
    @SerializedName("is_staff") val isStaff:  Boolean?,
)

data class AuthResponseDto(
    val access:   String,
    val refresh:  String,
    // El servidor devuelve los datos del usuario dentro de un objeto "user"
    @SerializedName("user") val user: UserDto?,
    // Compatibilidad: algunos endpoints devuelven los campos en el raíz
    @SerializedName("user_id")  val userId:   Int?,
    val username: String?,
    val email:    String?,
    @SerializedName("is_staff") val isStaff:  Boolean?,
) {
    // Helpers para obtener los datos sin importar la estructura del servidor
    fun resolvedUserId()   = user?.userId   ?: userId   ?: 0
    fun resolvedUsername() = user?.username ?: username ?: ""
    fun resolvedEmail()    = user?.email    ?: email    ?: ""
    fun resolvedIsStaff()  = user?.isStaff  ?: isStaff  ?: false
}

data class TokenRefreshResponseDto(
    val access:  String,
    val refresh: String?,
)

data class MeResponseDto(
    val id:         Int,
    val username:   String,
    val email:      String,
    @SerializedName("is_staff")     val isStaff:     Boolean,
    @SerializedName("is_superuser") val isSuperuser: Boolean,
)