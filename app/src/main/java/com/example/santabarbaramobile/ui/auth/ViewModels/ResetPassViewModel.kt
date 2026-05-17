package com.example.santabarbaramobile.ui.auth.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPassViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var email by mutableStateOf("")
    var code by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var isSuccess by mutableStateOf(false)
        private set

    fun resetPassword() {
        val cleanEmail = email.trim()
        val cleanCode = code.trim()
        val cleanPassword = newPassword.trim()

        if (cleanEmail.isBlank() || cleanCode.isBlank() || cleanPassword.isBlank()) {
            errorMessage = "Por favor, completa todos los campos."
            return
        }
        if (cleanPassword != confirmPassword.trim()) {
            errorMessage = "Las contraseñas no coinciden."
            return
        }
        if (cleanPassword.length < 6) {
            errorMessage = "La contraseña debe tener al menos 6 caracteres."
            return
        }
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            authRepository.resetPassword(cleanEmail, cleanCode, cleanPassword)
                .onSuccess { isSuccess = true }
                .onFailure { errorMessage = it.message }

            isLoading = false
        }
    }
}