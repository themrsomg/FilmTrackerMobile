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
class ChangePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var currentPassword by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var isSuccess by mutableStateOf(false)
        private set

    fun changePassword() {
        val current = currentPassword.trim()
        val new = newPassword.trim()
        val confirm = confirmPassword.trim()

        if (current.isBlank() || new.isBlank() || confirm.isBlank()) {
            errorMessage = "Por favor, completa todos los campos."
            return
        }
        if (new != confirm) {
            errorMessage = "La nueva contraseña y su confirmación no coinciden."
            return
        }
        if (new.length < 6) {
            errorMessage = "La nueva contraseña debe tener al menos 6 caracteres."
            return
        }
        if (current == new) {
            errorMessage = "La nueva contraseña no puede ser igual a la actual."
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            authRepository.changePassword(current, new)
                .onSuccess {
                    isSuccess = true
                    currentPassword = ""
                    newPassword = ""
                    confirmPassword = ""
                }
                .onFailure { errorMessage = it.message }

            isLoading = false
        }
    }

    fun clearStatus() {
        isSuccess = false
        errorMessage = null
    }
}