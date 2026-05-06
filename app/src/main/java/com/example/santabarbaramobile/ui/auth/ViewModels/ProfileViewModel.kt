package com.example.santabarbaramobile.ui.auth.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.repository.AuthRepository
import com.example.santabarbaramobile.data.repository.UserRepository
import com.example.santabarbaramobile.data.security.TokenManager
import com.example.santabarbaramobile.ui.auth.States.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    var uiState by mutableStateOf(ProfileState())
        private set

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            val token = tokenManager.getToken()

            if (token != null) {
                val result = userRepository.getUserProfile("Bearer $token")

                result.onSuccess { userResponse ->
                    uiState = uiState.copy(
                        username = userResponse.username,
                        email = userResponse.email,
                        isLoading = false
                    )
                }.onFailure { exception ->
                    uiState = uiState.copy(
                        error = exception.message ?: "Error desconocido",
                        isLoading = false
                    )
                }
            } else {
                uiState = uiState.copy(isLoading = false, error = "Token no encontrado")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearToken()
            uiState = ProfileState()
        }
    }
}