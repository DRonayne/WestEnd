package com.darach.westend.presentation.saved

import com.darach.westend.domain.repository.ShowRepository
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SavedShowsViewModelTest {

    private lateinit var viewModel: SavedShowsViewModel
    private lateinit var showRepository: ShowRepository

    @Before
    fun setup() {
        showRepository = mockk(relaxed = true)
        viewModel = SavedShowsViewModel(showRepository)
    }

    @Test
    fun `initial state should have default sort order`() = runTest {
        // When
        val initialState = viewModel.sortOrder.value

        // Then
        assertEquals(SavedShowsSortOrder.RECENTLY_SAVED, initialState)
    }

    @Test
    fun `updateSortOrder should update the sort order`() = runTest {
        // Given
        val newSortOrder = SavedShowsSortOrder.TITLE

        // When
        viewModel.updateSortOrder(newSortOrder)

        // Then
        assertEquals(newSortOrder, viewModel.sortOrder.value)
    }

}