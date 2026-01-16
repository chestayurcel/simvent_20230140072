package com.example.simvent.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
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
import com.example.simvent.data.model.AssetItem
import com.example.simvent.viewmodel.AssetUiState
import com.example.simvent.viewmodel.AssetViewModel
import com.example.simvent.viewmodel.ViewModelFactory
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetListScreen(
    onBack: () -> Unit,
    onAddAsset: () -> Unit,
    onEditAsset: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val assetViewModel: AssetViewModel = viewModel(factory = ViewModelFactory.Factory)
    val uiState = assetViewModel.assetUiState

    LaunchedEffect(Unit) {
        assetViewModel.getAssets()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Aset") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                actions = {
                    // Tombol Refresh
                    IconButton(onClick = { assetViewModel.getAssets() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddAsset) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Aset")
            }
        }
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding).fillMaxSize()) {

            // BAGIAN SEARCH & FILTER
            SearchAndFilterSection(assetViewModel)

            Divider(thickness = 1.dp, color = Color.LightGray)

            // BAGIAN LIST DATA
            Box(modifier = modifier.fillMaxSize()) {

                when (uiState) {
                    is AssetUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    is AssetUiState.Success -> {
                        AssetList(
                            assets = uiState.assets,
                            viewModel = assetViewModel,
                            onEdit = onEditAsset
                        )
                    }

                    is AssetUiState.Error -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = uiState.message, color = Color.Red)
                            Button(onClick = { assetViewModel.getAssets() }) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }
            }
        }
    }
}

// Komponen Search & Filter
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAndFilterSection(viewModel: AssetViewModel) {
    var showFilterMenu by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.padding(16.dp)) {
        // Baris Search
        OutlinedTextField(
            value = viewModel.searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            placeholder = { Text("Cari nama aset...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                // Ikon Filter di dalam Search Bar
                IconButton(onClick = { showFilterMenu = true }) {
                    Icon(
                        Icons.Filled.FilterAlt,
                        contentDescription = "Filter",
                        tint = if (viewModel.selectedFilterRoom != null) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }

                // Menu Dropdown Filter
                DropdownMenu(
                    expanded = showFilterMenu,
                    onDismissRequest = { showFilterMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Semua Ruangan (Reset)") },
                        onClick = {
                            viewModel.onFilterSelected(null) // Reset Filter
                            showFilterMenu = false
                        }
                    )
                    viewModel.roomList.forEach { room ->
                        DropdownMenuItem(
                            text = { Text(room.roomName) },
                            onClick = {
                                viewModel.onFilterSelected(room)
                                showFilterMenu = false
                            }
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            // Aksi saat tekan Enter di keyboard
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                viewModel.getAssets()
                focusManager.clearFocus() // Tutup keyboard
            }),
            shape = RoundedCornerShape(12.dp)
        )

        // Tampilkan Chip kecil jika Filter sedang aktif
        if (viewModel.selectedFilterRoom != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Filter: ", fontSize = 12.sp, color = Color.Gray)
                InputChip(
                    selected = true,
                    onClick = { viewModel.onFilterSelected(null) }, // Klik untuk hapus filter
                    label = { Text(viewModel.selectedFilterRoom!!.roomName) },
                    trailingIcon = {
                        Icon(Icons.Default.Close, contentDescription = "Hapus", modifier = Modifier.size(16.dp))
                    }
                )
            }
        }
    }
}

// Komponen Item List (Card)
@Composable
fun AssetList(
    assets: List<AssetItem>,
    viewModel: AssetViewModel,
    onEdit: (Int) -> Unit
) {
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
                AssetCard(asset, viewModel, onEdit)
            }
        }
    }
}

@Composable
fun AssetCard(
    asset: AssetItem,
    viewModel: AssetViewModel,
    onEdit: (Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // DIALOG KONFIRMASI HAPUS
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Hapus Aset?") },
            text = { Text("Apakah Anda yakin ingin menghapus '${asset.assetName}'?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAsset(
                        asset.assetId,
                        onSuccess = {
                            Toast.makeText(context, "Data Aset Terhapus", Toast.LENGTH_SHORT).show()
                        }
                    )
                    showDialog = false
                }) { Text("Ya", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Tidak") }
            }
        )
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = asset.assetName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Chip Lokasi (Badge Abu-abu kecil)
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = asset.roomName ?: "Tanpa Ruangan",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Tombol Edit/Delete
                Row {
                    IconButton(onClick = { onEdit(asset.assetId) }) {
                        Icon(Icons.Default.Edit, "Edit", tint = Color.Gray)
                    }
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Delete, "Hapus", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))

            // Footer: Qty & Kondisi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${asset.qty} ${asset.unit}",
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )

                // STATUS BADGE (Kapsul Warna)
                val isGood = asset.condition.equals("Baik", ignoreCase = true)
                val badgeColor = if (isGood) Color(0xFFE6F4EA) else Color(0xFFFCE8E6) // Background Muda
                val textColor = if (isGood) Color(0xFF137333) else Color(0xFFC5221F)  // Teks Tua

                Surface(
                    color = badgeColor,
                    shape = RoundedCornerShape(50) // Bentuk Kapsul
                ) {
                    Text(
                        text = asset.condition,
                        color = textColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}