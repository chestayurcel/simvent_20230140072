package com.example.simvent.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object ApiConfig {
    private const val BASE_URL = "http://10.0.2.2/simvent_api/"

    fun getApiService(): ApiService {

        // 1. Logger
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        // 2. Konfigurasi JSON Parser (Kotlinx Serialization)
        val jsonConfig = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        // 3. Setup Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(jsonConfig.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }
}