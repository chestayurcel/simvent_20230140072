package com.example.simvent.uicontroller

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.simvent.uicontroller.route.Screen
import com.example.simvent.view.AddAssetScreen
import com.example.simvent.view.AddRoomScreen
import com.example.simvent.view.AssetListScreen
import com.example.simvent.view.DashboardScreen
import com.example.simvent.view.LoginScreen
import com.example.simvent.view.RoomListScreen

@Composable
fun NavigationMap(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        // 1. RUTE LOGIN
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    // Pindah ke Dashboard, dan HAPUS riwayat Login
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // 2. RUTE DASHBOARD
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToAssets = {
                    navController.navigate(Screen.AssetList.route)
                },
                onNavigateToRooms = {
                    navController.navigate(Screen.RoomList.route)
                },
                onLogout = {
                    // Kembali ke Login, hapus semua riwayat backstack
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        // 3. RUTE ASSET LIST
        composable(Screen.AssetList.route) {
            AssetListScreen(
                onBack = {
                    navController.popBackStack()
                },
                onAddAsset = {
                    navController.navigate(Screen.AddAsset.route)
                }
            )
        }

        // 4. RUTE ADD ASSET
        composable(Screen.AddAsset.route) {
            AddAssetScreen(
                onBack = { navController.popBackStack() },
                onSuccess = {
                    // Mundur ke list & otomatis refresh (karena LaunchedEffect)
                    navController.popBackStack()
                }
            )
        }

        // 5. RUTE ROOM LIST
        composable(Screen.RoomList.route) {
             RoomListScreen(
                 onBack = { navController.popBackStack() },
                 onAddRoom = {
                     // Pindah ke halaman Add Room
                     navController.navigate(Screen.AddRoom.route)                 }
             )
        }

        // 6. RUTE ADD ROOM
        composable(Screen.AddRoom.route) {
            AddRoomScreen(
                onBack = { navController.popBackStack() },
                onSuccess = {
                    // Kalau sukses, mundur ke list & otomatis refresh
                    navController.popBackStack()
                }
            )
        }
    }
}