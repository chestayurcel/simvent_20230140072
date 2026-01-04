package com.example.simvent.data.repository

import com.example.simvent.data.model.AssetItem
import com.example.simvent.data.model.AssetResponse
import com.example.simvent.data.model.GeneralResponse
import com.example.simvent.data.network.ApiService

class AssetRepository(
    private val apiService: ApiService
) {
    // Helper untuk format token
    private fun generateBearer(token: String) = "Bearer $token"

    // 1. Ambil Data Aset
    suspend fun getAssets(token: String, page: Int = 1, search: String? = null, roomId: Int? = null): Result<AssetResponse> {
        return try {
            val response = apiService.getAssets(generateBearer(token), page, search, roomId)
            if (response.status == "success") {
                Result.success(response)
            } else {
                Result.failure(Exception(response.message ?: "Gagal memuat aset"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 2. Tambah Aset
    suspend fun createAsset(token: String, asset: AssetItem): Result<GeneralResponse> {
        return try {
            val response = apiService.createAsset(generateBearer(token), asset)
            if (response.status == "success") Result.success(response)
            else Result.failure(Exception(response.message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 3. Update Aset
    suspend fun updateAsset(token: String, asset: AssetItem): Result<GeneralResponse> {
        return try {
            val response = apiService.updateAsset(generateBearer(token), asset)
            if (response.status == "success") Result.success(response)
            else Result.failure(Exception(response.message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 4. Hapus Aset
    suspend fun deleteAsset(token: String, assetId: Int): Result<GeneralResponse> {
        return try {
            val idMap = mapOf("asset_id" to assetId)
            val response = apiService.deleteAsset(generateBearer(token), idMap)
            if (response.status == "success") Result.success(response)
            else Result.failure(Exception(response.message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 5. Ambil Detail Aset by ID
    suspend fun getAssetById(token: String, id: Int): Result<AssetItem> {
        return try {
            val result = getAssets(token)
            if (result.isSuccess) {
                val list = result.getOrNull()?.data ?: emptyList()
                val item = list.find { it.assetId == id }
                if (item != null) Result.success(item)
                else Result.failure(Exception("Aset tidak ditemukan"))
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Gagal ambil data"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}