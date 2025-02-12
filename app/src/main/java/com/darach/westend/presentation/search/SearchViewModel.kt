package com.darach.westend.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darach.westend.domain.model.Show
import com.darach.westend.domain.repository.ShowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    repository: ShowRepository
) : ViewModel() {

    private val _searchState = MutableStateFlow(SearchState())
    val searchState = _searchState.asStateFlow()

    private var allShows: List<Show> = emptyList()

    init {
        repository.getShows()
            .onEach { shows ->
                allShows = shows
                updateSearchResults(shows)
            }
            .launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String) {
        _searchState.update { it.copy(searchQuery = query) }
        refreshShows()
    }

    fun toggleCategory(category: String) {
        _searchState.update { state ->
            val updatedCategories = if (category in state.selectedCategories) {
                state.selectedCategories - category
            } else {
                state.selectedCategories + category
            }
            state.copy(selectedCategories = updatedCategories)
        }
        refreshShows()
    }

    fun updatePriceRange(range: ClosedFloatingPointRange<Float>) {
        _searchState.update { it.copy(priceRange = range) }
        refreshShows()
    }

    fun updateSortOption(option: SortOption) {
        _searchState.update { it.copy(sortOption = option) }
        refreshShows()
    }

    fun updateQuickFilter(filter: QuickFilter?) {
        _searchState.update { it.copy(quickFilter = filter) }
        refreshShows()
    }

    fun clearFilters() {
        _searchState.update {
            it.copy(
                searchQuery = "",
                selectedCategories = emptySet(),
                priceRange = 0f..80f,
                sortOption = SortOption.RELEVANCE,
                quickFilter = null
            )
        }
        refreshShows()
    }

    private fun refreshShows() {
        updateSearchResults(allShows)
    }

    private fun updateSearchResults(shows: List<Show>) {
        val state = _searchState.value

        val filteredShows = shows.asSequence()
            .filter { show ->
                state.searchQuery.isBlank() ||
                        show.title.contains(state.searchQuery, ignoreCase = true) ||
                        show.venue.contains(state.searchQuery, ignoreCase = true) ||
                        show.description.contains(state.searchQuery, ignoreCase = true)
            }
            .filter { show ->
                state.selectedCategories.isEmpty() || show.category in state.selectedCategories
            }
            .filter { show ->
                val price = show.priceRange.removePrefix("£").toFloatOrNull() ?: 0f
                price in state.priceRange
            }
            .filter { show ->
                state.quickFilter?.let { filter ->
                    when (filter) {
                        QuickFilter.SPECIAL_OFFERS -> show.specialOffer
                        QuickFilter.AWARD_WINNING -> show.awards.isNotEmpty()
                        QuickFilter.LAST_CHANCE -> show.status == "LastChance"
                    }
                } != false
            }
            .sortedWith(
                when (state.sortOption) {
                    SortOption.RELEVANCE -> compareBy { 0f }
                    SortOption.RATING_HIGH_TO_LOW -> compareByDescending { it.rating }
                    SortOption.PRICE_LOW_TO_HIGH -> compareBy {
                        it.priceRange.removePrefix("£").toFloatOrNull() ?: Float.POSITIVE_INFINITY
                    }

                    SortOption.PRICE_HIGH_TO_LOW -> compareByDescending {
                        it.priceRange.removePrefix("£").toFloatOrNull() ?: 0f
                    }
                }
            )
            .toList()

        _searchState.update { currentState ->
            currentState.copy(
                shows = filteredShows,
                resultCount = filteredShows.size,
                availableCategories = shows.map { it.category }.distinct().sorted()
            )
        }
    }
}

data class SearchState(
    val searchQuery: String = "",
    val selectedCategories: Set<String> = emptySet(),
    val priceRange: ClosedFloatingPointRange<Float> = 0f..80f,
    val sortOption: SortOption = SortOption.RELEVANCE,
    val quickFilter: QuickFilter? = null,
    val shows: List<Show> = emptyList(),
    val resultCount: Int = 0,
    val availableCategories: List<String> = emptyList()
)

enum class SortOption {
    RELEVANCE,
    RATING_HIGH_TO_LOW,
    PRICE_LOW_TO_HIGH,
    PRICE_HIGH_TO_LOW
}

enum class QuickFilter {
    SPECIAL_OFFERS,
    AWARD_WINNING,
    LAST_CHANCE
}