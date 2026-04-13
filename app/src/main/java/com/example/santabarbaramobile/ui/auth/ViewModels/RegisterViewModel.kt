package com.example.santabarbaramobile.ui.auth.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.ui.auth.States.RegisterState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val uiState = _uiState.asStateFlow()

    fun onRegisterAttempt(name: String, email: String, pass: String, confirmPass: String) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            _uiState.value = RegisterState.Error("Todos los campos son obligatorios")
            return
        }

        if (pass != confirmPass) {
            _uiState.value = RegisterState.Error("Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterState.Loading
            delay(2000)

            _uiState.value = RegisterState.Success("Cuenta creada para $name")
        }
    }

    fun resetState() {
        _uiState.value = RegisterState.Idle
    }
}