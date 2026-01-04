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
import com.example.simvent.viewmodel.AddRoomUiState
import com.example.simvent.viewmodel.AddRoomViewModel
import com.example.simvent.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoomScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddRoomViewModel = viewModel(factory = ViewModelFactory.Factory)
) {
    // State form
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    val uiState = viewModel.uiState
    val context = LocalContext.current

    // Pantau status: Kalau Success -> Mundur
    LaunchedEffect(uiState) {
        if (uiState is AddRoomUiState.Success) {
            Toast.makeText(context, "Ruangan berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            viewModel.resetState()
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Ruangan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Batal")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Input Nama
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Ruangan") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Input Deskripsi
            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Deskripsi (Opsional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Error Message
            if (uiState is AddRoomUiState.Error) {
                Text(uiState.message, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Tombol Simpan
            Button(
                onClick = { viewModel.addRoom(name, desc) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = name.isNotBlank() && uiState !is AddRoomUiState.Loading
            ) {
                if (uiState is AddRoomUiState.Loading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Simpan")
                }
            }
        }
    }
}