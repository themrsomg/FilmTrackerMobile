package com.example.santabarbaramobile.ui.auth.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.santabarbaramobile.ui.auth.States.ForgotPassState
import com.example.santabarbaramobile.ui.auth.ViewModels.ForgotPassViewModel

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPassViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var email by remember {
        mutableStateOf("")
    }
    var code by remember {
        mutableStateOf("")
    }
    var newPassword by remember {
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
        Text("Recuperar Acceso", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        when (state) {
            is ForgotPassState.Idle, is ForgotPassState.Error, is ForgotPassState.Loading -> {
                if (state !is ForgotPassState.EmailSent && state !is ForgotPassState.CodeVerified) {
                    StepSendEmail(email, {
                        email = it
                    }) {
                        viewModel.sendCode(email)
                    }
                }
            }
            is ForgotPassState.EmailSent -> {
                StepVerifyCode(code, {
                    code = it
                }) {
                    viewModel.verifyCode(code)
                }
            }
            is ForgotPassState.CodeVerified -> {
                StepNewPassword(newPassword, confirmPassword,
                    {
                        newPassword = it
                    }, {
                        confirmPassword = it
                    }) {
                    viewModel.resetPassword(newPassword, confirmPassword)
                }
            }
            is ForgotPassState.Success -> {
                Text("¡Contraseña actualizada!")
                Button(onClick = onNavigateBack) {
                    Text("Volver al Login")
                }
            }
        }

        if (state is ForgotPassState.Loading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        if (state is ForgotPassState.Error) {
            Text((state as ForgotPassState.Error).message, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun StepSendEmail(email: String, onEmailChange: (String) -> Unit, onSend: () -> Unit) {
    OutlinedTextField(value = email, onValueChange = onEmailChange, label = {
        Text("Tu Email")
                                                                            }, modifier = Modifier.fillMaxWidth())
    Button(onClick = onSend, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Text("Enviar Código")
    }
}

@Composable
fun StepVerifyCode(code: String, onCodeChange: (String) -> Unit, onVerify: () -> Unit) {
    OutlinedTextField(value = code, onValueChange = onCodeChange, label = {
        Text("Código de 4 dígitos")
                                                                          }, modifier = Modifier.fillMaxWidth())
    Button(onClick = onVerify, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Text("Verificar Código")
    }
}

@Composable
fun StepNewPassword(p1: String, p2: String, onP1: (String) -> Unit, onP2: (String) -> Unit, onReset: () -> Unit) {
    OutlinedTextField(value = p1, onValueChange = onP1, label = {
        Text("Nueva Contraseña")
                                                                }, modifier = Modifier.fillMaxWidth())
    OutlinedTextField(value = p2, onValueChange = onP2, label = {
        Text("Confirmar")
                                                                }, modifier = Modifier.fillMaxWidth())
    Button(onClick = onReset, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Text("Restablecer")
    }
}