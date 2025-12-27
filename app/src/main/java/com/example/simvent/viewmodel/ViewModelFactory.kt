package com.example.simvent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.simvent.SimventApplication
import com.example.simvent.di.AppContainer

object ViewModelFactory {
    val Factory = viewModelFactory {
        initializer {
            LoginViewModel(
                simventApplication().container.authRepository
            )
        }

        initializer {
            HomeViewModel(
                simventApplication().container.assetRepository,
                simventApplication().container.authRepository
            )
        }
    }
}

fun CreationExtras.simventApplication(): SimventApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SimventApplication)