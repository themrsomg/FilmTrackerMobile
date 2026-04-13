package com.example.santabarbaramobile.ui.auth.ViewModels
/*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.santabarbaramobile.data.model.Show
import com.example.santabarbaramobile.data.repository.ShowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ShowRepository
) : ViewModel() {
    private val _shows = MutableStateFlow<List<Show>>(emptyList())
    val shows = _shows.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getHomeData().onSuccess { _shows.value = it }
        }
    }
}
 */