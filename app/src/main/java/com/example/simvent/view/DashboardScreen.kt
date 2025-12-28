package com.example.simvent.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
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
import com.example.simvent.viewmodel.DashboardUiState
import com.example.simvent.viewmodel.DashboardViewModel
import com.example.simvent.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAssets: () -> Unit, // Tombol Kelola Aset
    onNavigateToRooms: () -> Unit,  // Tombol Kelola Ruangan
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(factory = ViewModelFactory.Factory)
) {
    val uiState = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SIMVENT Dashboard", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.logout(); onLogout() }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // LOGIKA TAMPILAN
            when (uiState) {
                is DashboardUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is DashboardUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(uiState.message, color = Color.Red)
                            Button(onClick = { viewModel.loadDashboardData() }) { Text("Coba Lagi") }
                        }
                    }
                }
                is DashboardUiState.Success -> {
                    // 1. HEADER
                    Text("Halo, Admin!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Berikut ringkasan inventaris Anda:", color = Color.Gray)

                    Spacer(modifier = Modifier.height(24.dp))

                    // 2. KARTU STATISTIK (Grid 2x2)
                    Row(Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(title = "Total Aset", count = uiState.totalAssets.toString(), color = Color(0xFF2196F3), modifier = Modifier.weight(1f))
                        StatCard(title = "Total Ruangan", count = uiState.totalRooms.toString(), color = Color(0xFFFF9800), modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(title = "Barang Kondisi Baik", count = uiState.goodCondition.toString(), color = Color(0xFF4CAF50), modifier = Modifier.weight(1f))
                        StatCard(title = "Barang Kondisi Rusak", count = uiState.badCondition.toString(), color = Color(0xFFF44336), modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 3. MENU UTAMA
                    Text("Menu Utama", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    MenuButton(text = "📦 Kelola Aset", onClick = onNavigateToAssets)
                    Spacer(modifier = Modifier.height(12.dp))
                    MenuButton(text = "🏢 Kelola Ruangan", onClick = onNavigateToRooms)
                }
            }
        }
    }
}

// Komponen Kartu Statistik
@Composable
fun StatCard(title: String, count: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(count, fontSize = 50.sp, fontWeight = FontWeight.Bold, color = color)
            Text(title, fontSize = 15.sp, color = Color.DarkGray)
        }
    }
}

// Komponen Tombol Menu
@Composable
fun MenuButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, fontSize = 16.sp)
    }
}