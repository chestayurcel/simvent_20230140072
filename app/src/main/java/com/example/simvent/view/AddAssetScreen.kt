package com.example.simvent.view

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.simvent.data.model.RoomItem
import com.example.simvent.viewmodel.AddAssetUiState
import com.example.simvent.viewmodel.AddAssetViewModel
import com.example.simvent.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAssetScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddAssetViewModel = viewModel(factory = ViewModelFactory.Factory)
) {
    // State Form
    var name by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("Unit") }
    var desc by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("Baik") }

    // State Dropdown Ruangan
    var expanded by remember { mutableStateOf(false) }
    var selectedRoom: RoomItem? by remember { mutableStateOf(null) }

    val uiState = viewModel.uiState
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        if (uiState is AddAssetUiState.Success) {
            Toast.makeText(context, "Aset berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
            viewModel.resetState()
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Aset Baru") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Nama Barang") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = qty, onValueChange = { qty = it },
                    label = { Text("Jumlah") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = unit, onValueChange = { unit = it },
                    label = { Text("Satuan") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            // DROPDOWN RUANGAN
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedRoom?.roomName ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Lokasi Ruangan") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (viewModel.roomList.isEmpty()) {
                        DropdownMenuItem(text = { Text("Tidak ada ruangan") }, onClick = {})
                    } else {
                        viewModel.roomList.forEach { room ->
                            DropdownMenuItem(
                                text = { Text(room.roomName) },
                                onClick = {
                                    selectedRoom = room
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text("Kondisi:", style = MaterialTheme.typography.bodyMedium)
            Row {
                listOf("Baik", "Rusak").forEach { item ->
                    Row(
                        modifier = Modifier.clickable { condition = item }.padding(end = 16.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        RadioButton(selected = (condition == item), onClick = { condition = item })
                        Text(item)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = desc, onValueChange = { desc = it },
                label = { Text("Keterangan (Opsional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 2
            )
            Spacer(modifier = Modifier.height(24.dp))

            // ERROR MESSAGE
            if (uiState is AddAssetUiState.Error) {
                Text(uiState.message, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    if (selectedRoom != null) {
                        viewModel.addAsset(name, qty, unit, condition, selectedRoom!!.roomId, desc)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = name.isNotBlank() && qty.isNotBlank() && selectedRoom != null && uiState !is AddAssetUiState.Loading
            ) {
                if (uiState is AddAssetUiState.Loading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Simpan")
                }
            }
        }
    }
}