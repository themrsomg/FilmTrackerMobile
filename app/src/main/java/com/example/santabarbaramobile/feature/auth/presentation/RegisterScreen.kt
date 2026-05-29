package com.example.santabarbaramobile.feature.auth.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.santabarbaramobile.feature.shows.domain.ResourceState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToMainHub: () -> Unit,
    onNavigateToConfirm: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val verificationState by viewModel.verificationState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showVerifyDialog by remember { mutableStateOf(false) }
    var registeredEmail by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is ResourceState.Success) {
            registeredEmail = (uiState as ResourceState.Success).data
            showVerifyDialog = true
        }
    }

    LaunchedEffect(verificationState) {
        when (verificationState) {
            is ResourceState.Success -> {
                viewModel.resetStates()
                showVerifyDialog = false
                onNavigateToConfirm(registeredEmail)
            }
            is ResourceState.Error -> {
                val errorMsg = (verificationState as ResourceState.Error).message
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                viewModel.resetStates()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Únete a FilmTracker",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre completo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = uiState !is ResourceState.Loading
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Nombre de usuario") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = uiState !is ResourceState.Loading
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = uiState !is ResourceState.Loading
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = uiState !is ResourceState.Loading
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = uiState !is ResourceState.Loading
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            viewModel.performRegistration(
                                name = name,
                                email = email,
                                username = username,
                                pass = password,
                                confirmPass = confirmPassword
                            )
                        },
                        enabled = uiState !is ResourceState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        if (uiState is ResourceState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Registrarme")
                        }
                    }

                    if (uiState is ResourceState.Error) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = (uiState as ResourceState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        if (showVerifyDialog) {
            AlertDialog(
                onDismissRequest = { /* No dejamos que lo cierre tocando afuera */ },
                title = { Text("¡Registro Exitoso!") },
                text = { Text("Tu cuenta ha sido creada correctamente.\n\nPara poder escribir reseñas y comentarios, necesitas verificar tu correo electrónico. ¿Deseas hacerlo ahora?") },
                confirmButton = {
                    Button(
                        onClick = { viewModel.sendVerificationCode(registeredEmail) },
                        enabled = verificationState !is ResourceState.Loading,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (verificationState is ResourceState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Verificar Ahora")
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showVerifyDialog = false
                            viewModel.resetStates()
                            onNavigateToMainHub()
                        },
                        enabled = verificationState !is ResourceState.Loading
                    ) {
                        Text("Más tarde")
                    }
                }
            )
        }
    }
}