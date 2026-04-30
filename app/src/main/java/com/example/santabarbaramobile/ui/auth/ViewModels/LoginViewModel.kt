package com.example.santabarbaramobile.ui.auth.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.remote.auth.LoginRequest
import com.example.santabarbaramobile.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<Resource<Unit>?>(null)
    val uiState: StateFlow<Resource<Unit>?> = _uiState.asStateFlow()

    fun performLogin(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = Resource.Error("Email y contraseña son obligatorios")
            return
        }

        viewModelScope.launch {
            _uiState.value = Resource.Loading
            try {
                val request = LoginRequest(email, pass)
                val response = repository.login(request)

                _uiState.value = Resource.Success(Unit)
            } catch (e: HttpException) {
                _uiState.value = Resource.Error("Credenciales inválidas. Verifica tus datos.")
            } catch (e: IOException) {
                _uiState.value = Resource.Error("Error de red. Revisa tu conexión.")
            } catch (e: Exception) {
                _uiState.value = Resource.Error("Ocurrió un error inesperado.")
            }
        }
    }
}