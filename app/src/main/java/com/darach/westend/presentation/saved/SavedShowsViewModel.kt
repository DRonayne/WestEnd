package com.darach.westend.presentation.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darach.westend.domain.repository.ShowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SavedShowsViewModel @Inject constructor(
    repository: ShowRepository
) : ViewModel() {

    private val _sortOrder = MutableStateFlow(SavedShowsSortOrder.RECENTLY_SAVED)
    val sortOrder = _sortOrder.asStateFlow()

    val savedShows = combine(
        repository.getSavedShows(),
        _sortOrder
    ) { shows, sortOrder ->
        when (sortOrder) {
            SavedShowsSortOrder.RECENTLY_SAVED -> shows.sortedByDescending { it.savedAt }
            SavedShowsSortOrder.TITLE -> shows.sortedBy { it.title }
            SavedShowsSortOrder.RATING -> shows.sortedByDescending { it.rating }
            SavedShowsSortOrder.VENUE -> shows.sortedBy { it.venue }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSortOrder(order: SavedShowsSortOrder) {
        _sortOrder.value = order
    }
}

enum class SavedShowsSortOrder {
    RECENTLY_SAVED,
    TITLE,
    RATING,
    VENUE
}