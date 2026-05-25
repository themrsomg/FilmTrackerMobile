package com.example.santabarbaramobile.feature.auth.presentation

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.core.network.NetworkErrorHandler
import com.example.santabarbaramobile.feature.auth.domain.LoginRequest
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
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<ResourceState<Unit>?>(null)
    val uiState: StateFlow<ResourceState<Unit>?> = _uiState.asStateFlow()

    fun performLogin(email: String, pass: String) {
        val normalizedEmail = email.trim()

        if (normalizedEmail.isBlank() || pass.isBlank()) {
            _uiState.value = ResourceState.Error("Email y contrasena son obligatorios")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(normalizedEmail).matches()) {
            _uiState.value = ResourceState.Error("Escribe un correo valido")
            return
        }

        viewModelScope.launch {
            _uiState.value = ResourceState.Loading
            repository.login(LoginRequest(normalizedEmail, pass))
                .onSuccess { response ->
                    tokenManager.saveSession(response.token, normalizedEmail)

                    _uiState.value = ResourceState.Success(Unit)
                }
                .onFailure { error ->
                    val friendlyError = NetworkErrorHandler.getFriendlyMessage(error)
                    _uiState.value = ResourceState.Error(friendlyError)
                }
        }
    }
}