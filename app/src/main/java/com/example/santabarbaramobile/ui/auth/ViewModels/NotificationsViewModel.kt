package com.example.santabarbaramobile.ui.auth.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.model.NotificationDto
import com.example.santabarbaramobile.data.repository.NotificationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val repository: NotificationsRepository
) : ViewModel() {

    var notifications by mutableStateOf<List<NotificationDto>>(emptyList())
        private set

    var unreadCount by mutableStateOf(0)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun fetchNotifications() {
        viewModelScope.launch {
            isLoading = true
            repository.getNotifications()
                .onSuccess { notifications = it }
                .onFailure { errorMessage = it.message }
            isLoading = false
        }
    }

    fun fetchUnreadCount() {
        viewModelScope.launch {
            repository.getUnreadCount().onSuccess { unreadCount = it }
        }
    }

    fun markAsRead(notificationId: Int) {
        notifications = notifications.map {
            if (it.id == notificationId) it.copy(readAt = "leido") else it
        }
        if (unreadCount > 0) unreadCount--

        viewModelScope.launch { repository.markAsRead(notificationId) }
    }

    fun markAllAsRead() {
        notifications = notifications.map { it.copy(readAt = "leido") }
        unreadCount = 0
        viewModelScope.launch { repository.markAllAsRead() }
    }
}