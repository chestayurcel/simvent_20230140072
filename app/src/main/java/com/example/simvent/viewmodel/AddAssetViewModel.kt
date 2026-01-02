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

sealed interface AddAssetUiState {
    object Idle : AddAssetUiState
    object Loading : AddAssetUiState
    object Success : AddAssetUiState
    data class Error(val message: String) : AddAssetUiState
}

class AddAssetViewModel(
    private val assetRepository: AssetRepository,
    private val roomRepository: RoomRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState: AddAssetUiState by mutableStateOf(AddAssetUiState.Idle)
        private set

    // Data untuk Dropdown Ruangan
    var roomList: List<RoomItem> by mutableStateOf(emptyList())
        private set

    init {
        loadRooms()
    }

    // Ambil daftar ruangan agar bisa dipilih di Dropdown
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

    fun addAsset(name: String, qty: String, unit: String, condition: String, roomId: Int, desc: String) {
        viewModelScope.launch {
            uiState = AddAssetUiState.Loading
            val token = authRepository.getSessionToken()

            if (token != null) {
                try {
                    val qtyInt = qty.toIntOrNull() ?: 0

                    val asset = AssetItem(
                        assetId = 0,
                        assetName = name,
                        qty = qtyInt,
                        unit = unit,
                        condition = condition,
                        entryDate = "",
                        itemDesc = desc,
                        roomId = roomId,
                        roomName = null
                    )

                    val result = assetRepository.createAsset(token, asset)

                    uiState = if (result.isSuccess) {
                        AddAssetUiState.Success
                    } else {
                        AddAssetUiState.Error(result.exceptionOrNull()?.message ?: "Gagal menyimpan")
                    }
                } catch (e: Exception) {
                    uiState = AddAssetUiState.Error(e.message ?: "Error input")
                }
            } else {
                uiState = AddAssetUiState.Error("Sesi habis")
            }
        }
    }

    fun resetState() {
        uiState = AddAssetUiState.Idle
    }
}