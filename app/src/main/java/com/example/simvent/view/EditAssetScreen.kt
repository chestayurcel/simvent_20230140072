package com.example.simvent.view

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simvent.viewmodel.EditAssetUiState
import com.example.simvent.viewmodel.EditAssetViewModel
import com.example.simvent.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAssetScreen(
    assetId: Int,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditAssetViewModel = viewModel(factory = ViewModelFactory.Factory)
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current

    // MUAT DATA SAAT PERTAMA KALI DIBUKA
    LaunchedEffect(Unit) {
        viewModel.loadInitialData(assetId)
    }

    LaunchedEffect(uiState) {
        if (uiState is EditAssetUiState.Success) {
            Toast.makeText(context, "Data Aset Berhasil Diperbarui", Toast.LENGTH_SHORT).show()
            viewModel.resetState()
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Aset") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Batal") }
                }
            )
        }
    ) { innerPadding ->

        // Tampilkan Loading saat ambil data awal
        if (uiState is EditAssetUiState.Loading && viewModel.name.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // Form Edit (Bind ke variabel di ViewModel)
            Column(
                modifier = modifier.padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = viewModel.name, onValueChange = { viewModel.name = it },
                    label = { Text("Nama Barang") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = viewModel.qty, onValueChange = { viewModel.qty = it },
                        label = { Text("Jumlah") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = viewModel.unit, onValueChange = { viewModel.unit = it },
                        label = { Text("Satuan") }, modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Dropdown Ruangan
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = viewModel.selectedRoom?.roomName ?: "", onValueChange = {}, readOnly = true,
                        label = { Text("Lokasi Ruangan") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        viewModel.roomList.forEach { room ->
                            DropdownMenuItem(
                                text = { Text(room.roomName) },
                                onClick = { viewModel.selectedRoom = room; expanded = false }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Kondisi
                Text("Kondisi:", style = MaterialTheme.typography.bodyMedium)
                Row {
                    listOf("Baik", "Rusak").forEach { item ->
                        Row(
                            modifier = Modifier.clickable { viewModel.condition = item }.padding(end = 16.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            RadioButton(selected = (viewModel.condition == item), onClick = { viewModel.condition = item })
                            Text(item)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = viewModel.desc, onValueChange = { viewModel.desc = it },
                    label = { Text("Keterangan") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Tombol Update
                Button(
                    onClick = { viewModel.updateAsset(assetId) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = viewModel.name.isNotEmpty() && viewModel.selectedRoom != null && uiState !is EditAssetUiState.Loading
                ) {
                    if (uiState is EditAssetUiState.Loading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                    else Text("Simpan")
                }
            }
        }
    }
}