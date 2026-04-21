package com.example.santabarbaramobile.ui.auth.ViewModels

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.repository.AuthRepository
import com.example.santabarbaramobile.ui.auth.States.ForgotPassState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

    private var generatedCode: String = ""

    fun sendCode(email: String) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = ForgotPassState.Error("Email no válido")
            return
        }

        _uiState.value = ForgotPassState.Loading

        viewModelScope.launch {
            // TODO: Cambiar por llamada real a authRepository.forgotPassword(email)
            delay(1500)

            generatedCode = "1234"
            _uiState.value = ForgotPassState.EmailSent
        }
    }

    fun verifyCode(code: String) {
        viewModelScope.launch {
            _uiState.value = ForgotPassState.Loading
            delay(1000)
            if (code == generatedCode) {
                _uiState.value = ForgotPassState.CodeVerified
            } else {
                _uiState.value = ForgotPassState.Error("Código incorrecto")
            }
        }
    }

    fun resetPassword(newPass: String, confirmPass: String) {
        if (newPass != confirmPass || newPass.length < 6) {
            _uiState.value = ForgotPassState.Error("Las contraseñas no coinciden o son muy cortas")
            return
        }

        viewModelScope.launch {
            _uiState.value = ForgotPassState.Loading
            // TODO: Llamada al repositorio para actualizar la clave (PATCH /api/auth/reset-password)
            delay(2000)
            _uiState.value = ForgotPassState.Success
        }
    }
}