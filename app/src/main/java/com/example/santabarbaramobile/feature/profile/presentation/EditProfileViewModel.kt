package com.example.santabarbaramobile.feature.profile.presentation

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.core.security.TokenManager
import com.example.santabarbaramobile.feature.profile.domain.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    var name by mutableStateOf("")
    var bio by mutableStateOf("")
    var currentImageUrl by mutableStateOf<String?>(null)
    var selectedImageUri by mutableStateOf<Uri?>(null)

    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var isSuccess by mutableStateOf(false)
        private set

    init {
        loadCurrentProfile()
    }

    private fun loadCurrentProfile() {
        viewModelScope.launch {
            isLoading = true
            val token = "Bearer ${tokenManager.getToken()}"
            userRepository.getUserProfile(token).onSuccess { user ->
                name = user.name ?: ""
                currentImageUrl = user.profileImage
            }.onFailure {
                errorMessage = "Error al cargar tu perfil."
            }
            isLoading = false
        }
    }

    fun saveProfile(context: Context) {
        if (name.isBlank()) {
            errorMessage = "El nombre no puede estar vacío."
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            val updateRes = userRepository.updateProfile(name, bio)
            if (updateRes.isFailure) {
                errorMessage = "Error al actualizar los datos."
                isLoading = false
                return@launch
            }
            selectedImageUri?.let { uri ->
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes() ?: byteArrayOf()
                    val requestBody = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    val part = MultipartBody.Part.createFormData("image", "profile.jpg", requestBody)

                    val photoRes = userRepository.uploadProfilePhoto(part)
                    if (photoRes.isFailure) {
                        errorMessage = "Datos guardados, pero falló al subir la foto."
                        isLoading = false
                        return@launch
                    }
                } catch (e: Exception) {
                    errorMessage = "Error al procesar la imagen de tu galería."
                    isLoading = false
                    return@launch
                }
            }

            isSuccess = true
            isLoading = false
        }
    }
}