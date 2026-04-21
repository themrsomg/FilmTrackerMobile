package com.example.santabarbaramobile.ui.auth.ViewModels

import androidx.lifecycle.ViewModel
import com.example.santabarbaramobile.data.security.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    fun logout() {
        tokenManager.clearToken()
    }
}