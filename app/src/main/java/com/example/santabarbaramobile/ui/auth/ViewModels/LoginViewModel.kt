package com.example.santabarbaramobile.ui.auth.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.ui.auth.States.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginState>(LoginState.Idle)
    val uiState = _uiState.asStateFlow()

    fun onLoginAttempt(email: String, pass: String) {
        if (email.isEmpty() || pass.isEmpty()) {
            _uiState.value = LoginState.Error("Campos vacíos")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginState.Loading
            delay(2000)
            _uiState.value = LoginState.Success("jwt_token_here")
        }
    }
}