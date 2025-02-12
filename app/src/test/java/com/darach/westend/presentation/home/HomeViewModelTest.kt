package com.darach.westend.presentation.home

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
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var showRepository: ShowRepository

    @Before
    fun setup() {
        showRepository = mockk(relaxed = true)
        viewModel = HomeViewModel(showRepository)
    }

    @Test
    fun `initial state should have default values`() = runTest {
        // When
        val initialState = viewModel.uiState.value

        // Then
        assertTrue(initialState.isLoading)
        assertTrue(initialState.initialLoad)
        assertNull(initialState.error)
    }

    @Test
    fun `refreshShows should update isLoading to true while refreshing`() = runTest {
        // Given
        coEvery { showRepository.refreshShows() } returns Unit

        // When
        viewModel.refreshShows()

        // Then
        assertTrue(viewModel.uiState.value.isLoading)
    }

}