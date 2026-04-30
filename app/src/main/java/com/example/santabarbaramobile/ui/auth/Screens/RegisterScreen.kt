package com.example.santabarbaramobile.ui.auth.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.santabarbaramobile.ui.auth.ViewModels.RegisterViewModel
import com.example.santabarbaramobile.ui.auth.ViewModels.Resource
import com.example.santabarbaramobile.ui.components.PasswordInputField

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Crear Cuenta",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Nombre de Usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                PasswordInputField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Contraseña"
                )

                Spacer(modifier = Modifier.height(8.dp))

                PasswordInputField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirmar Contraseña"
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.performRegistration(email, username, password, confirmPassword) },
                    enabled = uiState !is Resource.Loading,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    if (uiState is Resource.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Registrarse")
                    }
                }

                if (uiState is Resource.Error) {
                    Text(
                        text = (uiState as Resource.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }

    if (uiState is Resource.Success) {
        LaunchedEffect(Unit) {
            onNavigateBack()
        }
    }
}