package com.example.simvent.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simvent.data.model.RoomItem
import com.example.simvent.viewmodel.RoomUiState
import com.example.simvent.viewmodel.RoomViewModel
import com.example.simvent.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomListScreen(
    onBack: () -> Unit,
    onAddRoom: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoomViewModel = viewModel(factory = ViewModelFactory.Factory)
) {
    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.getRooms()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Ruangan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.getRooms() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh Data")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddRoom) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Ruangan")
            }
        }
    ) { innerPadding ->
        Box(modifier = modifier.padding(innerPadding).fillMaxSize()) {
            when (uiState) {
                is RoomUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is RoomUiState.Success -> {
                    if (uiState.rooms.isEmpty()) {
                        Text("Belum ada ruangan.", Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(contentPadding = PaddingValues(16.dp)) {
                            items(uiState.rooms) { room ->
                                RoomCard(room)
                            }
                        }
                    }
                }
                is RoomUiState.Error -> {
                    Text(uiState.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun RoomCard(room: RoomItem) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(room.roomName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            if (!room.roomDesc.isNullOrEmpty()) {
                Text(room.roomDesc, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}