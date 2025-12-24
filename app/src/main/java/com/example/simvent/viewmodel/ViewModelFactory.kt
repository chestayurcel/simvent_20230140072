package com.example.simvent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.simvent.SimventApplication
import com.example.simvent.di.AppContainer
// Import ViewModel nanti ditaruh di sini (akan error merah dulu sebentar)

object ViewModelFactory {

    val Factory = viewModelFactory {

        // 1. Persiapan untuk LoginViewModel (Nanti kita buat filenya)
        /* initializer {
            LoginViewModel(
                simventApplication().container.authRepository
            )
        }
        */

        // 2. Persiapan untuk HomeViewModel (Dashboard)
        /*
        initializer {
            HomeViewModel(
                simventApplication().container.authRepository,
                simventApplication().container.assetRepository
            )
        }
        */

        // Nanti kita uncomment kode di atas saat membuat ViewModel-nya
    }
}

// Fungsi ekstensi untuk mempersingkat pemanggilan Container
fun CreationExtras.simventApplication(): SimventApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SimventApplication)