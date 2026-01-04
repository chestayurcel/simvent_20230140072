package com.example.simvent.data.network

import com.example.simvent.data.model.*
import retrofit2.http.*

interface ApiService {
    // Otentikasi
    @POST("auth/login.php")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @GET("auth/validate.php")
    suspend fun checkSession(
        @Header("Authorization") token: String
    ): LoginResponse

    @POST("auth/logout.php")
    suspend fun logout(
        @Header("Authorization") token: String
    ): GeneralResponse

    // Manajemen Aset
    @GET("assets/read.php")
    suspend fun getAssets(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("search") search: String? = null,
        @Query("room_id") roomId: Int? = null
    ): AssetResponse

    @POST("assets/create.php")
    suspend fun createAsset(
        @Header("Authorization") token: String,
        @Body asset: AssetItem
    ): GeneralResponse

    @POST("assets/update.php")
    suspend fun updateAsset(
        @Header("Authorization") token: String,
        @Body asset: AssetItem
    ): GeneralResponse

    @POST("assets/delete.php")
    suspend fun deleteAsset(
        @Header("Authorization") token: String,
        @Body idMap: Map<String, Int>
    ): GeneralResponse

    // Manajemen Ruangan
    @GET("rooms/read.php")
    suspend fun getRooms(
        @Header("Authorization") token: String
    ): RoomResponse

    @POST("rooms/create.php")
    suspend fun createRoom(
        @Header("Authorization") token: String,
        @Body room: RoomItem
    ): GeneralResponse

    @POST("rooms/update.php")
    suspend fun updateRoom(
        @Header("Authorization") token: String,
        @Body room: RoomItem
    ): GeneralResponse

    @POST("rooms/delete.php")
    suspend fun deleteRoom(
        @Header("Authorization") token: String,
        @Body idMap: Map<String, Int>
    ): GeneralResponse
}