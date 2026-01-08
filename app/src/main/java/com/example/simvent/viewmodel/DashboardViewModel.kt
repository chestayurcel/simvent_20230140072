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
import retrofit2.HttpException

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
    object SessionExpired : DashboardUiState
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
                        val error = assetsResult.exceptionOrNull() ?: roomsResult.exceptionOrNull()
                        handleError(error)
                    }
                } catch (e: Exception) {
                    handleError(e)
                }
            } else {
                authRepository.logout() // Hapus sesi lokal
                uiState = DashboardUiState.SessionExpired
            }
        }
    }

    // Fungsi Helper untuk cek error 401
    private fun handleError(e: Throwable?) {
        if (e is HttpException && e.code() == 401) {
            // JIKA ERROR 401 (UNAUTHORIZED)
            viewModelScope.launch { authRepository.logout() }
            uiState = DashboardUiState.SessionExpired
        } else {
            // Error biasa (internet mati, server down, dll)
            uiState = DashboardUiState.Error(e?.message ?: "Terjadi kesalahan")
        }
    }

    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }
}