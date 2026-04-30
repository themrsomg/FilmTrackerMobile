package com.example.santabarbaramobile.ui.auth.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.remote.auth.RegisterRequest
import com.example.santabarbaramobile.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
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

    fun performRegistration(email: String, username: String, pass: String, confirmPass: String) {
        if (username.isBlank() || email.isBlank() || pass.isBlank() || confirmPass.isBlank()) {
            _uiState.value = Resource.Error("Todos los campos son obligatorios")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = Resource.Error("El formato del correo electrónico no es válido")
            return
        }

        if (pass.length < 6) {
            _uiState.value = Resource.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }

        if (pass != confirmPass) {
            _uiState.value = Resource.Error("Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            _uiState.value = Resource.Loading
            try {
                val request = RegisterRequest(email, username, pass)
                repository.register(request)
                _uiState.value = Resource.Success(Unit)
            } catch (e: HttpException) {
                _uiState.value = Resource.Error("Error del servidor: Verifica tus datos.")
            } catch (e: IOException) {
                _uiState.value = Resource.Error("Error de red: El servidor cerró la conexión.", e)
            } catch (e: Exception) {
                _uiState.value = Resource.Error("Error inesperado: ${e.message}")
            }
        }
    }
}