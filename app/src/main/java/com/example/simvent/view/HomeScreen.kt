package com.example.simvent.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simvent.data.model.AssetItem
import com.example.simvent.viewmodel.HomeUiState
import com.example.simvent.viewmodel.HomeViewModel
import com.example.simvent.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel(factory = ViewModelFactory.Factory)
) {
    val uiState = homeViewModel.homeUiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Aset") },
                actions = {
                    // Tombol Refresh
                    IconButton(onClick = { homeViewModel.getAssets() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    // Tombol Logout
                    IconButton(onClick = {
                        homeViewModel.logout()
                        onLogout()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Aset")
            }
        }
    ) { innerPadding ->

        Box(modifier = modifier.padding(innerPadding).fillMaxSize()) {

            when (uiState) {
                is HomeUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is HomeUiState.Success -> {
                    AssetList(assets = uiState.assets)
                }
                is HomeUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = uiState.message, color = Color.Red)
                        Button(onClick = { homeViewModel.getAssets() }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
        }
    }
}

// Komponen Item List (Card)
@Composable
fun AssetList(assets: List<AssetItem>) {
    if (assets.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Belum ada data aset.")
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(assets) { asset ->
                AssetCard(asset)
            }
        }
    }
}

@Composable
fun AssetCard(asset: AssetItem) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = asset.assetName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Qty: ${asset.qty} ${asset.unit}")
                Text(
                    text = asset.condition,
                    color = if (asset.condition == "Baik") Color.Green else Color.Red
                )
            }
            Text(text = "Ruang: ${asset.roomName ?: "-"}", fontSize = 12.sp, color = Color.Gray)
        }
    }
}