package com.example.simvent.data.repository

import com.example.simvent.data.local.UserPreferences
import com.example.simvent.data.model.LoginRequest
import com.example.simvent.data.model.LoginResponse
import com.example.simvent.data.network.ApiService

class AuthRepository(
    private val apiService: ApiService,
    private val pref: UserPreferences
) {

    // Login
    suspend fun login(req: LoginRequest): Result<LoginResponse> {
        return try {
            val response = apiService.login(req)
            if (response.status == "success" && response.data != null) {
                // Simpan sesi terenkripsi
                pref.saveSession(response.data.token, response.data.fullName)
                Result.success(response)
            } else {
                Result.failure(Exception(response.message ?: "Login Gagal"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Cek Token
    fun getSessionToken(): String? {
        return pref.getToken()
    }

    // Logout
    suspend fun logout(): Result<String> {
        return try {
            val token = pref.getToken()
            if (token != null) {
                apiService.logout("Bearer $token")
            }
            pref.clearSession()
            Result.success("Logout Berhasil")
        } catch (e: Exception) {
            pref.clearSession()
            Result.failure(e)
        }
    }
}