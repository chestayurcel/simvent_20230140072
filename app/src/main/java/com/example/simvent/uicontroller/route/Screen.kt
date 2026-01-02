package com.example.simvent.uicontroller.route

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object AssetList : Screen("asset_list")
    object RoomList : Screen("room_list")
    object AddRoom : Screen("add_room")
    object AddAsset : Screen("add_asset")
}