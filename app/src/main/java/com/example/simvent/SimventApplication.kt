package com.example.simvent

import android.app.Application
import com.example.simvent.di.AppContainer
import com.example.simvent.di.DefaultAppContainer

class SimventApplication : Application() {

    // Instance Container yang bisa diakses dari mana saja
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        // Saat aplikasi start, buat Container-nya
        container = DefaultAppContainer(this)
    }
}