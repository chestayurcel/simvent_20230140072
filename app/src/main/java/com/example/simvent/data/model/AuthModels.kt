package com.example.simvent.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

// 1. Model untuk mengirim data Login (Body Request)
@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

// 2. Model untuk menerima respon Login dari PHP
@Serializable
data class LoginResponse(
    val status: String,
    val message: String? = null,
    val data: UserSession? = null
)

// 3. Detail Data User (isi dari "data")
@Serializable
data class UserSession(
    @SerialName("user_id") val userId: Int,
    @SerialName("full_name") val fullName: String,
    val token: String,
    @SerialName("expired_at") val expiredAt: String
)