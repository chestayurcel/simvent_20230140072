package com.example.simvent.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simvent.data.model.RoomItem
import com.example.simvent.data.repository.AuthRepository
import com.example.simvent.data.repository.RoomRepository
import kotlinx.coroutines.launch

sealed interface RoomUiState {
    object Loading : RoomUiState
    data class Success(val rooms: List<RoomItem>) : RoomUiState
    data class Error(val message: String) : RoomUiState
}

class RoomViewModel(
    private val roomRepository: RoomRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState: RoomUiState by mutableStateOf(RoomUiState.Loading)
        private set

    init {
        getRooms()
    }

    fun getRooms() {
        viewModelScope.launch {
            uiState = RoomUiState.Loading
            val token = authRepository.getSessionToken()

            if (token != null) {
                val result = roomRepository.getRooms(token)
                uiState = if (result.isSuccess) {
                    val rooms = result.getOrNull()?.data ?: emptyList()
                    RoomUiState.Success(rooms)
                } else {
                    RoomUiState.Error(result.exceptionOrNull()?.message ?: "Gagal memuat ruangan")
                }
            } else {
                uiState = RoomUiState.Error("Sesi habis")
            }
        }
    }

    fun deleteRoom(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val token = authRepository.getSessionToken()

            if (token != null) {
                val result = roomRepository.deleteRoom(token, id)
                if (result.isSuccess) {
                    onSuccess()  // Panggil callback Toast
                    getRooms()   // Refresh data list otomatis
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Gagal menghapus"
                    onError(errorMsg)
                }
            } else {
                onError("Sesi habis")
            }
        }
    }
}