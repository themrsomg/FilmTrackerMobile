package com.example.santabarbaramobile.ui.auth.ViewModels

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.repository.AuthRepository
import com.example.santabarbaramobile.ui.auth.States.ForgotPassState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPassViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ForgotPassState>(ForgotPassState.Idle)
    val uiState = _uiState.asStateFlow()

    fun sendCode(email: String) {
        val normalizedEmail = email.trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(normalizedEmail).matches()) {
            _uiState.value = ForgotPassState.Error("Email no valido")
            return
        }

        viewModelScope.launch {
            _uiState.value = ForgotPassState.Loading
            authRepository.forgotPassword(normalizedEmail)
                .onSuccess { _uiState.value = ForgotPassState.Success }
                .onFailure { error ->
                    _uiState.value = ForgotPassState.Error(
                        error.message ?: "No se pudo enviar el correo de recuperacion"
                    )
                }
        }
    }

    fun verifyCode(code: String) {
        _uiState.value = ForgotPassState.Error(
            "La recuperacion se completa desde el enlace enviado por correo."
        )
    }

    fun resetPassword(newPass: String, confirmPass: String) {
        _uiState.value = ForgotPassState.Error(
            "Abre el enlace del correo para crear una contrasena nueva."
        )
    }
}
