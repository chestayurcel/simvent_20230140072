package com.example.simvent.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simvent.data.model.AssetItem
import com.example.simvent.data.model.RoomItem
import com.example.simvent.data.repository.AssetRepository
import com.example.simvent.data.repository.AuthRepository
import com.example.simvent.data.repository.RoomRepository
import kotlinx.coroutines.launch

sealed interface AssetUiState {
    object Loading : AssetUiState
    data class Success(val assets: List<AssetItem>) : AssetUiState
    data class Error(val message: String) : AssetUiState
}

class AssetViewModel(
    private val assetRepository: AssetRepository,
    private val roomRepository: RoomRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var assetUiState: AssetUiState by mutableStateOf(AssetUiState.Loading)
        private set

    // State untuk filter dan pencarian
    var searchQuery by mutableStateOf("") // Teks pencarian
    var selectedFilterRoom: RoomItem? by mutableStateOf(null) // Filter Ruangan
    var roomList: List<RoomItem> by mutableStateOf(emptyList())

    init {
        loadRooms()
        getAssets()
    }

    // Ambil daftar ruangan untuk opsi Filter
    private fun loadRooms() {
        viewModelScope.launch {
            val token = authRepository.getSessionToken()
            if (token != null) {
                val result = roomRepository.getRooms(token)
                if (result.isSuccess) {
                    roomList = result.getOrNull()?.data ?: emptyList()
                }
            }
        }
    }

    // Fungsi Ambil Data
    fun getAssets() {
        viewModelScope.launch {
            assetUiState = AssetUiState.Loading

            val token = authRepository.getSessionToken()

            if (token != null) {
                // Kirim parameter search dan roomId ke Repository
                val result = assetRepository.getAssets(
                    token = token,
                    page = 1,
                    search = if (searchQuery.isBlank()) null else searchQuery,
                    roomId = selectedFilterRoom?.roomId // Kirim ID ruangan (bisa null)
                )

                assetUiState = if (result.isSuccess) {
                    val assets = result.getOrNull()?.data ?: emptyList()
                    AssetUiState.Success(assets)
                } else {
                    AssetUiState.Error(result.exceptionOrNull()?.message ?: "Gagal memuat data")
                }
            } else {
                assetUiState = AssetUiState.Error("Sesi habis")
            }
        }
    }

    // Fungsi helper untuk UI
    fun onSearchQueryChanged(newQuery: String) {
        searchQuery = newQuery
    }

    fun onFilterSelected(room: RoomItem?) {
        selectedFilterRoom = room
        getAssets()
    }

    // Fungsi Hapus Data
    fun deleteAsset(assetId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            assetUiState = AssetUiState.Loading

            val token = authRepository.getSessionToken()
            if (token != null) {
                val result = assetRepository.deleteAsset(token, assetId)
                if (result.isSuccess) {
                    // Panggil Callback sukses
                    onSuccess()
                    // Refresh data list
                    getAssets()
                } else {
                    assetUiState = AssetUiState.Error(result.exceptionOrNull()?.message ?: "Gagal menghapus")
                }
            } else {
                assetUiState = AssetUiState.Error("Sesi habis")
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