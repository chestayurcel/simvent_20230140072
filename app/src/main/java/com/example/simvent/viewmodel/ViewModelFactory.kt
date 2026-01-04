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
            DashboardViewModel(
                simventApplication().container.assetRepository,
                simventApplication().container.roomRepository,
                simventApplication().container.authRepository
            )
        }

        initializer {
            AssetViewModel(
                simventApplication().container.assetRepository,
                simventApplication().container.authRepository
            )
        }

        initializer {
            RoomViewModel(
                simventApplication().container.roomRepository,
                simventApplication().container.authRepository
            )
        }

        initializer {
            AddRoomViewModel(
                simventApplication().container.roomRepository,
                simventApplication().container.authRepository
            )
        }

        initializer {
            AddAssetViewModel(
                simventApplication().container.assetRepository,
                simventApplication().container.roomRepository,
                simventApplication().container.authRepository
            )
        }

        initializer {
            EditAssetViewModel(
                simventApplication().container.assetRepository,
                simventApplication().container.roomRepository,
                simventApplication().container.authRepository
            )
        }

        initializer {
            EditRoomViewModel(
                simventApplication().container.roomRepository,
                simventApplication().container.authRepository
            )
        }
    }
}

fun CreationExtras.simventApplication(): SimventApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SimventApplication)