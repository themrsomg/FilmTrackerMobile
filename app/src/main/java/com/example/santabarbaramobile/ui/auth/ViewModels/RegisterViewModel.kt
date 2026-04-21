package com.example.santabarbaramobile.ui.auth.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.remote.auth.RegisterRequest
import com.example.santabarbaramobile.data.repository.AuthRepository
import com.example.santabarbaramobile.ui.auth.States.RegisterState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

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


        _uiState.value = RegisterState.Loading

        viewModelScope.launch {
            val result = authRepository.register(RegisterRequest(email, pass))

            result.onSuccess {
                _uiState.value = RegisterState.Success("Cuenta creada exitosamente para $email. ¡Ya puedes iniciar sesión!")
            }.onFailure { error ->
                _uiState.value = RegisterState.Error(error.message ?: "El usuario ya existe o hubo un error en el servidor.")
            }
        }
    }

    fun resetState() {
        _uiState.value = RegisterState.Idle
    }
}