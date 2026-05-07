package com.example.santabarbaramobile.ui.auth.ViewModels

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.remote.auth.RegisterRequest
import com.example.santabarbaramobile.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String, val cause: Exception? = null) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<Resource<Unit>?>(null)
    val uiState: StateFlow<Resource<Unit>?> = _uiState.asStateFlow()

    fun performRegistration(
        name: String,
        email: String,
        username: String,
        pass: String,
        confirmPass: String
    ) {
        val normalizedName = name.trim()
        val normalizedEmail = email.trim()
        val normalizedUsername = username.trim().lowercase()

        if (
            normalizedName.isBlank() ||
            normalizedEmail.isBlank() ||
            normalizedUsername.isBlank() ||
            pass.isBlank() ||
            confirmPass.isBlank()
        ) {
            _uiState.value = Resource.Error("Todos los campos son obligatorios")
            return
        }

        if (normalizedName.length !in 2..40) {
            _uiState.value = Resource.Error("El nombre debe tener entre 2 y 40 caracteres")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(normalizedEmail).matches()) {
            _uiState.value = Resource.Error("El formato del correo electronico no es valido")
            return
        }

        if (!Regex("^[a-zA-Z0-9._]{3,30}$").matches(normalizedUsername)) {
            _uiState.value = Resource.Error("El username debe tener entre 3 y 30 caracteres")
            return
        }

        if (pass.length < 6) {
            _uiState.value = Resource.Error("La contrasena debe tener al menos 6 caracteres")
            return
        }

        if (pass != confirmPass) {
            _uiState.value = Resource.Error("Las contrasenas no coinciden")
            return
        }

        viewModelScope.launch {
            _uiState.value = Resource.Loading
            repository.register(
                RegisterRequest(
                    email = normalizedEmail,
                    name = normalizedName,
                    username = normalizedUsername,
                    password = pass
                )
            )
                .onSuccess { _uiState.value = Resource.Success(Unit) }
                .onFailure { error ->
                    _uiState.value = Resource.Error(error.message ?: "No se pudo crear la cuenta")
                }
        }
    }
}
