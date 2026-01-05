package com.example.simvent.data.repository

import com.example.simvent.data.model.GeneralResponse
import com.example.simvent.data.model.RoomItem
import com.example.simvent.data.model.RoomResponse
import com.example.simvent.data.network.ApiService
import retrofit2.HttpException
import org.json.JSONObject

class RoomRepository(
    private val apiService: ApiService
) {
    private fun generateBearer(token: String) = "Bearer $token"

    // 1. Ambil Semua Ruangan
    suspend fun getRooms(token: String): Result<RoomResponse> {
        return try {
            val response = apiService.getRooms(generateBearer(token))
            if (response.status == "success") {
                Result.success(response)
            } else {
                Result.failure(Exception(response.message ?: "Gagal memuat ruangan"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 2. Buat Ruangan Baru
    suspend fun createRoom(token: String, name: String, desc: String): Result<GeneralResponse> {
        return try {
            val room = RoomItem(0, name, desc)
            val response = apiService.createRoom(generateBearer(token), room)

            if (response.status == "success") Result.success(response)
            else Result.failure(Exception(response.message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 3. Update Ruangan
    suspend fun updateRoom(token: String, roomId: Int, name: String, desc: String): Result<GeneralResponse> {
        return try {
            val roomData = RoomItem(
                roomId = roomId,
                roomName = name,
                roomDesc = desc
            )

            val response = apiService.updateRoom("Bearer $token", roomData)
            if (response.status == "success") Result.success(response)
            else Result.failure(Exception(response.message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 4. Hapus Ruangan
    suspend fun deleteRoom(token: String, roomId: Int): Result<GeneralResponse> {
        return try {
            val idMap = mapOf("room_id" to roomId)

            val response = apiService.deleteRoom("Bearer $token", idMap)
            if (response.status == "success") Result.success(response)
            else Result.failure(Exception(response.message))

        } catch (e: HttpException) {
            val jsonString = e.response()?.errorBody()?.string()

            val customMessage = try {
                val jsonObject = JSONObject(jsonString)
                jsonObject.getString("message")
            } catch (ex: Exception) {
                e.message() ?: "Terjadi kesalahan server"
            }

            Result.failure(Exception(customMessage))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 5. Ambil Ruangan Berdasarkan ID
    suspend fun getRoomById(token: String, id: Int): Result<RoomItem> {
        return try {
            val result = getRooms(token)
            if (result.isSuccess) {
                val list = result.getOrNull()?.data ?: emptyList()
                val item = list.find { it.roomId == id }
                if (item != null) Result.success(item)
                else Result.failure(Exception("Ruangan tidak ditemukan"))
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Gagal ambil data"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}