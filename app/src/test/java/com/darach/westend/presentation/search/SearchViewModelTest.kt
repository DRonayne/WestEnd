package com.darach.westend.presentation.search

import com.darach.westend.domain.model.Show
import com.darach.westend.domain.repository.ShowRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private lateinit var viewModel: SearchViewModel
    private lateinit var showRepository: ShowRepository

    @Before
    fun setup() {
        showRepository = mockk(relaxed = true)
        viewModel = SearchViewModel(showRepository)
    }

    @Test
    fun `updateSearchQuery should update search state with new query`() = runTest {
        // Given
        val query = "Test Query"
        val shows = listOf(
            Show(
                title = "Test Show",
                venue = "Test Venue",
                category = "Test Category",
                bookingUntil = "2025-12-31",
                description = "Test Description",
                runningTime = 120,
                minimumAge = 18,
                priceRange = "£20",
                showTimes = "19:00",
                status = "Open",
                imageUrl = "http://test.com",
                showType = "Musical"
            )
        )
        coEvery { showRepository.getShows() } returns flowOf(shows)

        // When
        viewModel.updateSearchQuery(query)

        // Then
        assertEquals(query, viewModel.searchState.value.searchQuery)
    }

    @Test
    fun `toggleCategory should add category if not already selected`() = runTest {
        // Given
        val category = "Test Category"

        // When
        viewModel.toggleCategory(category)

        // Then
        assertTrue(viewModel.searchState.value.selectedCategories.contains(category))
    }

    @Test
    fun `toggleCategory should remove category if already selected`() = runTest {
        // Given
        val category = "Test Category"
        viewModel.toggleCategory(category) // Add category first

        // When
        viewModel.toggleCategory(category) // Toggle to remove

        // Then
        assertFalse(viewModel.searchState.value.selectedCategories.contains(category))
    }

    @Test
    fun `updatePriceRange should update search state with new price range`() = runTest {
        // Given
        val priceRange = 10f..50f

        // When
        viewModel.updatePriceRange(priceRange)

        // Then
        assertEquals(priceRange, viewModel.searchState.value.priceRange)
    }

    @Test
    fun `updateSortOption should update search state with new sort option`() = runTest {
        // Given
        val sortOption = SortOption.RATING_HIGH_TO_LOW

        // When
        viewModel.updateSortOption(sortOption)

        // Then
        assertEquals(sortOption, viewModel.searchState.value.sortOption)
    }

    @Test
    fun `updateQuickFilter should update search state with new quick filter`() = runTest {
        // Given
        val quickFilter = QuickFilter.SPECIAL_OFFERS

        // When
        viewModel.updateQuickFilter(quickFilter)

        // Then
        assertEquals(quickFilter, viewModel.searchState.value.quickFilter)
    }

    @Test
    fun `clearFilters should reset search state to default values`() = runTest {
        // Given
        viewModel.updateSearchQuery("Test Query")
        viewModel.toggleCategory("Test Category")
        viewModel.updatePriceRange(10f..50f)
        viewModel.updateSortOption(SortOption.RATING_HIGH_TO_LOW)
        viewModel.updateQuickFilter(QuickFilter.SPECIAL_OFFERS)

        // When
        viewModel.clearFilters()

        // Then
        val state = viewModel.searchState.value
        assertEquals("", state.searchQuery)
        assertTrue(state.selectedCategories.isEmpty())
        assertEquals(0f..80f, state.priceRange)
        assertEquals(SortOption.RELEVANCE, state.sortOption)
        assertNull(state.quickFilter)
    }

    @Test
    fun `initial state should have default values`() = runTest {
        // When
        val initialState = viewModel.searchState.value

        // Then
        assertEquals("", initialState.searchQuery)
        assertTrue(initialState.selectedCategories.isEmpty())
        assertEquals(0f..80f, initialState.priceRange)
        assertEquals(SortOption.RELEVANCE, initialState.sortOption)
        assertNull(initialState.quickFilter)
        assertTrue(initialState.shows.isEmpty())
        assertEquals(0, initialState.resultCount)
        assertTrue(initialState.availableCategories.isEmpty())
    }

    @Test
    fun `clearFilters should reset all filters to default values`() = runTest {
        // Given
        viewModel.updateSearchQuery("Test Query")
        viewModel.toggleCategory("Test Category")
        viewModel.updatePriceRange(10f..50f)
        viewModel.updateSortOption(SortOption.RATING_HIGH_TO_LOW)
        viewModel.updateQuickFilter(QuickFilter.SPECIAL_OFFERS)

        // When
        viewModel.clearFilters()

        // Then
        val state = viewModel.searchState.value
        assertEquals("", state.searchQuery)
        assertTrue(state.selectedCategories.isEmpty())
        assertEquals(0f..80f, state.priceRange)
        assertEquals(SortOption.RELEVANCE, state.sortOption)
        assertNull(state.quickFilter)
    }

    @Test
    fun `empty shows list should result in empty search results`() = runTest {
        // Given
        coEvery { showRepository.getShows() } returns flowOf(emptyList())

        // When
        val state = viewModel.searchState.value

        // Then
        assertTrue(state.shows.isEmpty())
        assertEquals(0, state.resultCount)
        assertTrue(state.availableCategories.isEmpty())
    }

    @Test
    fun `no matching shows should result in empty search results`() = runTest {
        // Given
        val shows = listOf(
            Show(
                title = "Test Show 1",
                venue = "Venue A",
                category = "Category A",
                bookingUntil = "2025-12-31",
                description = "Description A",
                runningTime = 120,
                minimumAge = 18,
                priceRange = "£20",
                showTimes = "19:00",
                status = "Open",
                imageUrl = "http://test.com",
                showType = "Musical"
            ),
            Show(
                title = "Another Show",
                venue = "Venue B",
                category = "Category B",
                bookingUntil = "2025-12-31",
                description = "Description B",
                runningTime = 90,
                minimumAge = 12,
                priceRange = "£30",
                showTimes = "20:00",
                status = "Open",
                imageUrl = "http://test.com",
                showType = "Play"
            )
        )
        coEvery { showRepository.getShows() } returns flowOf(shows)

        // When
        viewModel.updateSearchQuery("Non-existent Query")
        viewModel.toggleCategory("Non-existent Category")
        viewModel.updatePriceRange(100f..200f)
        viewModel.updateQuickFilter(QuickFilter.SPECIAL_OFFERS)

        // Then
        val state = viewModel.searchState.value
        assertEquals(0, state.resultCount)
        assertTrue(state.shows.isEmpty())
    }

}