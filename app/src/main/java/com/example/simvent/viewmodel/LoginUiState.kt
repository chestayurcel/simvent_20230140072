package com.example.simvent.viewmodel

sealed interface LoginUiState {
    object Idle : LoginUiState          // Status awal
    object Loading : LoginUiState       // Sedang memutar loading spinner
    data class Success(val message: String) : LoginUiState // Login berhasil
    data class Error(val message: String) : LoginUiState   // Login gagal
}