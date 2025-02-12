package com.darach.westend.presentation.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darach.westend.domain.model.Show
import com.darach.westend.domain.repository.ShowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ShowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    val shows = repository.getShows()
        .map { it.groupBy(Show::status) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    init {
        refreshShows()
    }

    fun refreshShows() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }
        try {
            withContext(Dispatchers.IO) { repository.refreshShows() }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message) }
        } finally {
            delay(1500) // show off loading animation
            _uiState.update { it.copy(isLoading = false, initialLoad = false) }
        }
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val initialLoad: Boolean = true,
    val error: String? = null
)