package com.darach.westend.presentation.show

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darach.westend.domain.model.Show
import com.darach.westend.domain.repository.ShowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowDetailsViewModel @Inject constructor(
    private val repository: ShowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShowDetailsUiState())
    val uiState = _uiState.asStateFlow()

    fun loadShow(title: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaved = repository.isShowSaved(title)) }

            repository.getShows()
                .collect { shows ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            show = shows.find { it.title == title },
                            recommendedShows = shows
                                .filter { it.title != title }
                                .shuffled()
                                .take(5)
                        )
                    }
                }
        }
    }

    fun toggleSave() {
        viewModelScope.launch {
            uiState.value.show?.let { show ->
                repository.toggleSaveShow(show.title)
                _uiState.update { it.copy(isSaved = !it.isSaved) }
            }
        }
    }
}

data class ShowDetailsUiState(
    val show: Show? = null,
    val isSaved: Boolean = false,
    val recommendedShows: List<Show> = emptyList()
)