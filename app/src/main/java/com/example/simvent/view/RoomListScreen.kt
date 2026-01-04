package com.example.simvent.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    onEditRoom: (Int) -> Unit,
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
                                RoomCard(room, viewModel, onEditRoom)
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
fun RoomCard(
    room: RoomItem,
    viewModel: RoomViewModel,
    onEdit: (Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Dialog Konfirmasi Hapus
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Hapus Ruangan?") },
            text = { Text("Yakin hapus ruangan'${room.roomName}'? Aset di dalamnya mungkin ikut terhapus atau kehilangan referensi.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteRoom(room.roomId) {
                        Toast.makeText(context, "Ruangan Berhasil Dihapus", Toast.LENGTH_SHORT).show()
                    }
                    showDialog = false
                }) { Text("Ya", color = Color.Red) }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Tidak") } }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(room.roomName, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                // Tombol Aksi (Edit & Delete)
                Row {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Delete, "Hapus", tint = Color.Red)
                    }
                    IconButton(onClick = { onEdit(room.roomId) }) { // Panggil Callback Edit
                        Icon(Icons.Default.Edit, "Edit", tint = Color.Gray)
                    }
                }
            }
            if (!room.roomDesc.isNullOrEmpty()) {
                Text(room.roomDesc, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}