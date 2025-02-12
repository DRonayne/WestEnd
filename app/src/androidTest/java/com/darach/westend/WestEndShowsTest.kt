package com.darach.westend

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class WestEndShowsTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val TIMEOUT_MS = 1000L
    private val TAG_SEARCH_INPUT = "search_input"
    private val TAG_SHOW_CARD = "show_card"
    private val TAG_SEARCH_SHOW_CARD = "search_show_card"
    private val TAG_SAVE_BUTTON = "save_button"
    private val TAG_FILTER_BUTTON = "filter_button"
    private val TAG_PRICE_SLIDER = "price_slider"

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun homeScreen_displaysCorrectInitialState() {
        composeRule.apply {

            onNodeWithText("West End Shows").assertIsDisplayed()
            onNodeWithText("Featured Shows").assertIsDisplayed()
            onNodeWithText("Trending Now").assertIsDisplayed()
            onNodeWithText("Home").assertIsDisplayed()
            onNodeWithText("Search").assertIsDisplayed()
            onNodeWithText("Saved").assertIsDisplayed()
            onNodeWithText("Settings").assertIsDisplayed()
        }
    }

    @Test
    fun search_showsRelevantResults() {
        composeRule.apply {

            onNodeWithText("Search").performClick()
            onNodeWithTag(TAG_SEARCH_INPUT).performTextInput("Lion King")

            waitUntil(timeoutMillis = TIMEOUT_MS) {
                onAllNodesWithTag(TAG_SEARCH_SHOW_CARD).fetchSemanticsNodes().isNotEmpty()
            }

            onNodeWithText("The Lion King", useUnmergedTree = true).assertExists()
            onNodeWithText("Lyceum Theatre", useUnmergedTree = true).assertExists()
            onNodeWithText("£", substring = true, useUnmergedTree = true).assertExists()
        }
    }

    @Test
    fun filters_applyCorrectly() {
        composeRule.apply {

            onNodeWithText("Search").performClick()
            onNodeWithTag(TAG_FILTER_BUTTON).performClick()

            onNode(hasText("Musical Favourites") and hasClickAction()).performClick()
            onNode(hasText("Musical Favourites") and hasTestTag("category_chip") and isSelected()).assertExists()

            onNodeWithTag(TAG_PRICE_SLIDER).performTouchInput {
                swipeRight(
                    startX = 0f,
                    endX = center.x
                )
            }

            onNode(
                hasTestTag(TAG_SEARCH_SHOW_CARD) and hasAnyDescendant(hasText("Les Misérables")),
                useUnmergedTree = true
            ).assertExists()
        }
    }

    @Test
    fun savedShows_manageCorrectly() {
        composeRule.apply {

            onAllNodesWithTag(TAG_SHOW_CARD)[0].performClick()
            onNodeWithTag(TAG_SAVE_BUTTON).performClick()

            activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }

            onNodeWithText("Saved").performClick()
            onAllNodesWithTag(TAG_SEARCH_SHOW_CARD)[0].performClick()
            onNodeWithTag(TAG_SAVE_BUTTON).performClick()

            activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }

            onNodeWithText("Saved").performClick()
            onNodeWithText("No saved shows yet").assertIsDisplayed()
        }
    }

    @Test
    fun showDetails_displaysCorrectInformation() {
        composeRule.apply {

            onAllNodesWithTag(TAG_SHOW_CARD)[0].performClick()

            onNodeWithText("About the Show").assertIsDisplayed()
            onNodeWithText("Show Times").assertExists()
            onNodeWithText("Important Information").assertExists()

            onNodeWithTag(TAG_SAVE_BUTTON).assertExists()

            onNodeWithContentDescription("Share").assertExists()
        }
    }
}