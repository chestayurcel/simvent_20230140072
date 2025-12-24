package com.example.simvent.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class UserPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences

    init {
        // Membuat Master Key
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        // Inisialisasi EncryptedSharedPreferences
        // Nama file: "session_prefs"
        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "session_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, // Enkripsi Key
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM // Enkripsi Value
        )
    }

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_NAME = "user_name"
    }

    // Fungsi Simpan Sesi
    fun saveSession(token: String, name: String) {
        sharedPreferences.edit().apply {
            putString(KEY_TOKEN, token)
            putString(KEY_NAME, name)
            apply()
        }
    }

    // Fungsi Ambil Token
    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    // Fungsi Ambil Nama
    fun getName(): String? {
        return sharedPreferences.getString(KEY_NAME, null)
    }

    // Fungsi Logout
    fun clearSession() {
        sharedPreferences.edit().apply {
            clear()
            apply()
        }
    }
}