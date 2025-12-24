package com.example.simvent.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// 1. Respon Utama saat GET Rooms
@Serializable
data class RoomResponse(
    val status: String,
    val message: String? = null,
    val data: List<RoomItem>? = null
)

// 2. Objek Ruangan
@Serializable
data class RoomItem(
    @SerialName("room_id") val roomId: Int,
    @SerialName("room_name") val roomName: String,
    @SerialName("room_desc") val roomDesc: String? = null
)