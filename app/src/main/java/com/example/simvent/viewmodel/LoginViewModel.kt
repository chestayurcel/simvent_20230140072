package com.example.simvent.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simvent.data.model.LoginRequest
import com.example.simvent.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    var loginUiState: LoginUiState by mutableStateOf(LoginUiState.Idle)
        private set

    fun login(username: String, pass: String) {
        if (username.isBlank() || pass.isBlank()) {
            loginUiState = LoginUiState.Error("Username dan Password tidak boleh kosong")
            return
        }

        loginUiState = LoginUiState.Loading

        viewModelScope.launch {
            val request = LoginRequest(username, pass)

            val result = authRepository.login(request)

            loginUiState = if (result.isSuccess) {
                LoginUiState.Success("Login Berhasil!")
            } else {
                LoginUiState.Error(result.exceptionOrNull()?.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun resetState() {
        loginUiState = LoginUiState.Idle
    }
}