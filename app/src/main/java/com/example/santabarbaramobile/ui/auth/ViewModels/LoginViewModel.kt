package com.example.santabarbaramobile.ui.auth.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.remote.auth.LoginRequest
import com.example.santabarbaramobile.data.repository.AuthRepository
import com.example.santabarbaramobile.ui.auth.States.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginState>(LoginState.Idle)
    val uiState = _uiState.asStateFlow()

    fun onLoginAttempt(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = LoginState.Error("Campos vacíos")
            return
        }

        _uiState.value = LoginState.Loading

        viewModelScope.launch {
            val result = authRepository.login(LoginRequest(email, pass))
            result.onSuccess { response ->
                _uiState.value = LoginState.Success(response.token)
            }.onFailure { error ->
                _uiState.value = LoginState.Error(error.message ?: "Error de autenticación")
            }
        }
    }
}