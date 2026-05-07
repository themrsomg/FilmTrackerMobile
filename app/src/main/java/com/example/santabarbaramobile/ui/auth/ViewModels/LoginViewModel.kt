package com.example.santabarbaramobile.ui.auth.ViewModels

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.remote.auth.LoginRequest
import com.example.santabarbaramobile.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<Resource<Unit>?>(null)
    val uiState: StateFlow<Resource<Unit>?> = _uiState.asStateFlow()

    fun performLogin(email: String, pass: String) {
        val normalizedEmail = email.trim()

        if (normalizedEmail.isBlank() || pass.isBlank()) {
            _uiState.value = Resource.Error("Email y contrasena son obligatorios")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(normalizedEmail).matches()) {
            _uiState.value = Resource.Error("Escribe un correo valido")
            return
        }

        viewModelScope.launch {
            _uiState.value = Resource.Loading
            repository.login(LoginRequest(normalizedEmail, pass))
                .onSuccess { _uiState.value = Resource.Success(Unit) }
                .onFailure { error ->
                    _uiState.value = Resource.Error(error.message ?: "No se pudo iniciar sesion")
                }
        }
    }
}
