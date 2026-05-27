package com.example.santabarbaramobile.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.feature.auth.domain.AuthRepository
import com.example.santabarbaramobile.core.security.TokenManager
import com.example.santabarbaramobile.feature.auth.domain.ConfirmAccountState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import com.example.santabarbaramobile.core.network.NetworkErrorHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmAccountViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<ConfirmAccountState>(ConfirmAccountState.Idle)
    val uiState = _uiState.asStateFlow()

    fun verify(email: String, code: String) {
        if (code.length < 6) {
            _uiState.value = ConfirmAccountState.Error("El código debe tener 6 dígitos")
            return
        }

        viewModelScope.launch {
            _uiState.value = ConfirmAccountState.Loading
            repository.verifyEmail(email, code)
                .onSuccess {
                    tokenManager.clearToken()
                    _uiState.value = ConfirmAccountState.Success("Cuenta verificada con éxito. Redirigiendo al Login...")
                }
                .onFailure { error ->
                    _uiState.value = ConfirmAccountState.Error(NetworkErrorHandler.getFriendlyMessage(error))
                }
        }
    }

    fun resendCode(email: String) {
        viewModelScope.launch {
            _uiState.value = ConfirmAccountState.Loading
            repository.resendVerification(email)
                .onSuccess {
                    _uiState.value = ConfirmAccountState.ResendSuccess("Se ha reenviado el código a tu correo.")
                }
                .onFailure { error ->
                    _uiState.value = ConfirmAccountState.Error(NetworkErrorHandler.getFriendlyMessage(error))
                }
        }
    }

    fun resetState() {
        _uiState.value = ConfirmAccountState.Idle
    }
}