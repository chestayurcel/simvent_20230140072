package com.example.simvent.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simvent.data.repository.AssetRepository
import com.example.simvent.data.repository.AuthRepository
import com.example.simvent.data.repository.RoomRepository
import kotlinx.coroutines.launch

sealed interface DashboardUiState {
    object Loading : DashboardUiState
    data class Success(
        val totalAssets: Int,
        val totalRooms: Int,
        val goodCondition: Int,
        val badCondition: Int,
        val userName: String
    ) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

class DashboardViewModel(
    private val assetRepository: AssetRepository,
    private val roomRepository: RoomRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState: DashboardUiState by mutableStateOf(DashboardUiState.Loading)
        private set

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            uiState = DashboardUiState.Loading
            val token = authRepository.getSessionToken()
            val name = authRepository.getName() ?: "Admin"

            if (token != null) {
                try {
                    // 1. Ambil Data Aset & Ruangan secara paralel (atau berurutan)
                    val assetsResult = assetRepository.getAssets(token)
                    val roomsResult = roomRepository.getRooms(token)

                    if (assetsResult.isSuccess && roomsResult.isSuccess) {
                        val assets = assetsResult.getOrNull()?.data ?: emptyList()
                        val rooms = roomsResult.getOrNull()?.data ?: emptyList()

                        // 2. Hitung Statistik Manual di sini
                        val totalAssets = assets.size
                        val totalRooms = rooms.size
                        val good = assets.count { it.condition.equals("Baik", ignoreCase = true) }
                        val bad = assets.count { it.condition.equals("Rusak", ignoreCase = true) }

                        uiState = DashboardUiState.Success(
                            totalAssets = totalAssets,
                            totalRooms = totalRooms,
                            goodCondition = good,
                            badCondition = bad,
                            userName = name
                        )
                    } else {
                        uiState = DashboardUiState.Error("Gagal memuat data statistik")
                    }
                } catch (e: Exception) {
                    uiState = DashboardUiState.Error(e.message ?: "Terjadi kesalahan")
                }
            } else {
                uiState = DashboardUiState.Error("Sesi habis")
            }
        }
    }

    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }
}