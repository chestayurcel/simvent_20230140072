package com.example.simvent.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simvent.viewmodel.LoginUiState
import com.example.simvent.viewmodel.LoginViewModel
import com.example.simvent.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = viewModel(factory = ViewModelFactory.Factory)
) {
    // State untuk Input User
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Ambil State dari ViewModel
    val uiState = loginViewModel.loginUiState
    val context = LocalContext.current

    // Efek Samping (Side Effect) untuk memantau perubahan status
    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUiState.Success -> {
                Toast.makeText(context, uiState.message, Toast.LENGTH_SHORT).show()
                onLoginSuccess() // Panggil fungsi navigasi ke Home
                loginViewModel.resetState()
            }
            is LoginUiState.Error -> {
                Toast.makeText(context, uiState.message, Toast.LENGTH_LONG).show()
                loginViewModel.resetState()
            }
            else -> {} // Idle atau Loading
        }
    }

    // TAMPILAN UI (Layout)
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SIMVENT",
            fontSize = 32.sp,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Sistem Inventaris Masuk",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(48.dp))

        // INPUT USERNAME
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // INPUT PASSWORD
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(), // Ubah huruf jadi titik-titik
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // TOMBOL LOGIN / LOADING
        if (uiState is LoginUiState.Loading) {
            CircularProgressIndicator() // Putaran Loading
        } else {
            Button(
                onClick = { loginViewModel.login(username, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = username.isNotBlank() && password.isNotBlank()
            ) {
                Text(text = "Masuk", fontSize = 18.sp)
            }
        }
    }
}