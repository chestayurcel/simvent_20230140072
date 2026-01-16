package com.example.simvent.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MeetingRoom
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush

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

    // Auto-refresh
    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    LaunchedEffect(uiState) {
        if (uiState is DashboardUiState.SessionExpired) {
            onLogout()
        }
    }

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
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Total Aset",
                            count = uiState.totalAssets.toString(),
                            color = Color(0xFF2196F3),
                            icon = Icons.Default.Inventory,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Total Ruangan",
                            count = uiState.totalRooms.toString(),
                            color = Color(0xFFFF9800),
                            icon = Icons.Default.MeetingRoom,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Kondisi Baik",
                            count = uiState.goodCondition.toString(),
                            color = Color(0xFF4CAF50),
                            icon = Icons.Default.CheckCircle,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Kondisi Rusak",
                            count = uiState.badCondition.toString(),
                            color = Color(0xFFF44336),
                            icon = Icons.Default.Warning,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 24.dp, horizontal = 8.dp),
                        thickness = 1.dp,
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )

                    // 3. MENU UTAMA
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Menu Utama", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SquareMenuButton(
                            text = "Kelola Aset",
                            icon = Icons.Default.Inventory,
                            onClick = onNavigateToAssets,
                            modifier = Modifier.weight(1f)
                        )
                        SquareMenuButton(
                            text = "Kelola Ruangan",
                            icon = Icons.Default.MeetingRoom,
                            onClick = onNavigateToRooms,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                is DashboardUiState.SessionExpired -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

// Komponen Kartu Statistik
@Composable
fun StatCard(
    title: String,
    count: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Dekorasi (Icon Transparan di Pojok)
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 10.dp, y = 10.dp),
                tint = color.copy(alpha = 0.05f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Baris Atas: Icon Badge & Label
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(color.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = color
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        lineHeight = 14.sp
                    )
                }

                // Angka Statistik
                Text(
                    text = count,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2D3142)
                )
            }
        }
    }
}

// Komponen Tombol Menu
@Composable
fun SquareMenuButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1f),
        shape = RoundedCornerShape(20.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        contentPadding = PaddingValues(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}