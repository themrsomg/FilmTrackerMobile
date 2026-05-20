package com.example.santabarbaramobile.feature.auth.presentation

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.feature.auth.domain.AuthRepository
import com.example.santabarbaramobile.feature.auth.domain.ForgotPassState
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
            _uiState.value = ForgotPassState.Error("Email no válido")
            return
        }

        viewModelScope.launch {
            _uiState.value = ForgotPassState.Loading
            authRepository.forgotPassword(normalizedEmail)
                .onSuccess {
                    _uiState.value = ForgotPassState.Success
                }
                .onFailure { error ->
                    _uiState.value = ForgotPassState.Error(
                        error.message ?: "No se pudo procesar la solicitud"
                    )
                }
        }
    }
}