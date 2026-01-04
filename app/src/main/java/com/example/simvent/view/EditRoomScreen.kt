package com.example.simvent.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simvent.viewmodel.EditRoomUiState
import com.example.simvent.viewmodel.EditRoomViewModel
import com.example.simvent.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRoomScreen(
    roomId: Int,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditRoomViewModel = viewModel(factory = ViewModelFactory.Factory)
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current

    // Load data saat layar dibuka
    LaunchedEffect(Unit) { viewModel.loadRoom(roomId) }

    // Pantau sukses
    LaunchedEffect(uiState) {
        if (uiState is EditRoomUiState.Success) {
            Toast.makeText(context, "Data Berhasil Diperbarui", Toast.LENGTH_SHORT).show()
            viewModel.resetState()
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Ruangan") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Batal") } }
            )
        }
    ) { innerPadding ->
        // Tampilkan loading jika data belum siap
        if (uiState is EditRoomUiState.Loading && viewModel.name.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(modifier.padding(innerPadding).padding(16.dp)) {
                OutlinedTextField(
                    value = viewModel.name, onValueChange = { viewModel.name = it },
                    label = { Text("Nama Ruangan") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = viewModel.desc, onValueChange = { viewModel.desc = it },
                    label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth(), minLines = 3
                )
                Spacer(modifier = Modifier.height(24.dp))

                if (uiState is EditRoomUiState.Error) Text((uiState as EditRoomUiState.Error).message, color = MaterialTheme.colorScheme.error)

                Button(
                    onClick = { viewModel.updateRoom(roomId) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = viewModel.name.isNotBlank() && uiState !is EditRoomUiState.Loading
                ) {
                    if (uiState is EditRoomUiState.Loading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                    else Text("Simpan Perubahan")
                }
            }
        }
    }
}