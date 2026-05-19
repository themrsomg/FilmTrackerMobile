package com.example.santabarbaramobile.ui.auth.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.repository.AuthRepository
import com.example.santabarbaramobile.data.security.TokenManager
import com.example.santabarbaramobile.ui.auth.States.ConfirmAccountState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
                    tokenManager.markAsVerifiedLocally()
                    _uiState.value = ConfirmAccountState.Success
                }
                .onFailure { error ->
                    _uiState.value = ConfirmAccountState.Error(error.message ?: "Código incorrecto")
                }
        }
    }

    fun resendCode(email: String) {
        viewModelScope.launch {
            _uiState.value = ConfirmAccountState.Loading
            delay(1500)
            _uiState.value = ConfirmAccountState.Idle
        }
    }
}