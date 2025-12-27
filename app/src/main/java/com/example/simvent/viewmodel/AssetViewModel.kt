package com.example.simvent.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simvent.data.model.AssetItem
import com.example.simvent.data.repository.AssetRepository
import com.example.simvent.data.repository.AuthRepository
import kotlinx.coroutines.launch

// Status Tampilan Home
sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val assets: List<AssetItem>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val assetRepository: AssetRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var homeUiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    init {
        getAssets()
    }

    // Fungsi Ambil Data
    fun getAssets() {
        viewModelScope.launch {
            homeUiState = HomeUiState.Loading

            // 1. Ambil Token dulu
            val token = authRepository.getSessionToken()

            if (token != null) {
                // 2. Panggil API getAssets
                val result = assetRepository.getAssets(token)

                homeUiState = if (result.isSuccess) {
                    val assets = result.getOrNull()?.data ?: emptyList()
                    HomeUiState.Success(assets)
                } else {
                    HomeUiState.Error(result.exceptionOrNull()?.message ?: "Gagal memuat data")
                }
            } else {
                HomeUiState.Error("Sesi habis, silakan login ulang")
            }
        }
    }

    // Fungsi Logout
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}