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

sealed interface AssetUiState {
    object Loading : AssetUiState
    data class Success(val assets: List<AssetItem>) : AssetUiState
    data class Error(val message: String) : AssetUiState
}

class AssetViewModel(
    private val assetRepository: AssetRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var assetUiState: AssetUiState by mutableStateOf(AssetUiState.Loading)
        private set

    init {
        getAssets()
    }

    // Fungsi Ambil Data
    fun getAssets() {
        viewModelScope.launch {
            assetUiState = AssetUiState.Loading

            // 1. Ambil Token dulu
            val token = authRepository.getSessionToken()

            if (token != null) {
                // 2. Panggil API getAssets
                val result = assetRepository.getAssets(token)

                assetUiState = if (result.isSuccess) {
                    val assets = result.getOrNull()?.data ?: emptyList()
                    AssetUiState.Success(assets)
                } else {
                    AssetUiState.Error(result.exceptionOrNull()?.message ?: "Gagal memuat data")
                }
            } else {
                assetUiState = AssetUiState.Error("Sesi habis, silakan login ulang")
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