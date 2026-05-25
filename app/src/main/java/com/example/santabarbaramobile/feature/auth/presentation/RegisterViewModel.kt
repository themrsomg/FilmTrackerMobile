package com.example.santabarbaramobile.feature.auth.presentation

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.core.network.NetworkErrorHandler
import com.example.santabarbaramobile.feature.auth.domain.LoginRequest
import com.example.santabarbaramobile.feature.auth.domain.RegisterRequest
import com.example.santabarbaramobile.feature.auth.domain.AuthRepository
import com.example.santabarbaramobile.core.security.TokenManager
import com.example.santabarbaramobile.feature.profile.domain.HomeUiState
import com.example.santabarbaramobile.feature.shows.domain.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<ResourceState<String>?>(null)
    val uiState: StateFlow<ResourceState<String>?> = _uiState.asStateFlow()

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

        if (normalizedName.length !in 2..40) {
            _uiState.value = ResourceState.Error("El nombre debe tener entre 2 y 40 caracteres")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(normalizedEmail).matches()) {
            _uiState.value = ResourceState.Error("El formato del correo electronico no es valido")
            return
        }

        if (!Regex("^[a-zA-Z0-9._]{3,30}$").matches(normalizedUsername)) {
            _uiState.value = ResourceState.Error("El username debe tener entre 3 y 30 caracteres")
            return
        }

        if (pass.length < 6) {
            _uiState.value = ResourceState.Error("La contrasena debe tener al menos 6 caracteres")
            return
        }

        if (pass != confirmPass) {
            _uiState.value = ResourceState.Error("Las contrasenas no coinciden")
            return
        }

        viewModelScope.launch {
            _uiState.value = ResourceState.Loading
            repository.register(
                RegisterRequest(
                    email = normalizedEmail,
                    name = normalizedName,
                    username = normalizedUsername,
                    password = pass
                )
            )
                .onSuccess {
                    repository.login(LoginRequest(normalizedEmail, pass))
                        .onSuccess { loginResponse ->
                            tokenManager.saveSession(loginResponse.token, normalizedEmail)
                            _uiState.value = ResourceState.Success(normalizedEmail)
                        }
                        .onFailure { error ->
                            _uiState.value = ResourceState.Error("Registro exitoso, pero falló el auto-login: ${error.message}")
                        }
                }
                .onFailure { error ->
                    val friendlyError = NetworkErrorHandler.getFriendlyMessage(error)
                    _uiState.value = ResourceState.Error(friendlyError)
                }
        }
    }
}