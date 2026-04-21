package com.example.santabarbaramobile.ui.auth.States

import com.example.santabarbaramobile.data.model.Show

data class ShowDetailState(
    val isLoading: Boolean = true,
    val show: Show? = null,
    val error: String? = null
)