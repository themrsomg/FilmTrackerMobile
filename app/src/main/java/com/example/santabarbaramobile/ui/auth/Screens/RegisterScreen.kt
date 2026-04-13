package com.example.santabarbaramobile.ui.auth.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.santabarbaramobile.ui.auth.States.RegisterState
import com.example.santabarbaramobile.ui.auth.ViewModels.RegisterViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var name by remember {
        mutableStateOf("")
    }
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var confirmPassword by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Crear Cuenta", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                            },
            label = {
                Text("Nombre Completo")
                    },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                            },
            label = {
                Text("Correo Electrónico")
                    },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                            },
            label = {
                Text("Contraseña")
                    },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                            },
            label = {
                Text("Confirmar Contraseña")
                    },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.onRegisterAttempt(name, email, password, confirmPassword)
                      },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = state !is RegisterState.Loading
        ) {
            if (state is RegisterState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Registrarse")
            }
        }

        TextButton(onClick = onNavigateBack) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }

        when (state) {
            is RegisterState.Error -> {
                Text((state as RegisterState.Error).message, color = MaterialTheme.colorScheme.error)
            }
            is RegisterState.Success -> {
                AlertDialog(
                    onDismissRequest = {
                        viewModel.resetState(); onNavigateBack()
                                       },
                    confirmButton = {
                        TextButton(onClick = onNavigateBack) {
                            Text("OK")
                        }
                                    },
                    title = {
                        Text("¡Éxito!")
                            },
                    text = {
                        Text((state as RegisterState.Success).message)
                    }
                )
            }
            else -> {
                Text("Algo ha salido mal, inténtalo más tarde. Ajua!")
            }
        }
    }
}