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
        @Body asset: AssetItem // Kita bisa reuse AssetItem atau buat CreateAssetRequest khusus
    ): GeneralResponse

    @POST("assets/update.php")
    suspend fun updateAsset(
        @Header("Authorization") token: String,
        @Body asset: AssetItem
    ): GeneralResponse

    @POST("assets/delete.php")
    suspend fun deleteAsset(
        @Header("Authorization") token: String,
        @Body idMap: Map<String, Int> // Kirim {"asset_id": 123}
    ): GeneralResponse
}