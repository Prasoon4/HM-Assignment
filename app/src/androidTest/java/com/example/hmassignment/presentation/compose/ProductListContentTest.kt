package com.example.hmassignment.presentation.compose

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hmassignment.R
import com.example.hmassignment.presentation.ProductUiModel
import com.example.hmassignment.presentation.SwatchDisplayInfo
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class ProductListContentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun productGrid_isDisplayed() {
        setContent(uiModels = buildUiModels(3))

        composeTestRule
            .onNodeWithTag(ProductListContentTestTags.PRODUCT_LIST_GRID)
            .assertIsDisplayed()
    }

    @Test
    fun productGrid_rendersAllItems() {
        setContent(uiModels = buildUiModels(4))

        composeTestRule
            .onAllNodesWithTag(ProductListContentTestTags.PRODUCT_ITEM)
            .assertCountEquals(4)
    }

    @Test
    fun scrollToTopFab_isVisible_whenCurrentPageIsGreaterThan3() {
        setContent(currentPage = 4)

        composeTestRule
            .onNodeWithTag(ProductListContentTestTags.SCROLL_TO_TOP_FAB)
            .assertIsDisplayed()
    }

    @Test
    fun scrollToTopFab_isNotDisplayed_atBoundary_currentPageIs3() {
        setContent(currentPage = 3)

        composeTestRule
            .onNodeWithTag(ProductListContentTestTags.SCROLL_TO_TOP_FAB)
            .assertDoesNotExist()
    }

    @Test
    fun scrollToTopFab_isNotDisplayed_whenCurrentPageIs1() {
        setContent(currentPage = 1)

        composeTestRule
            .onNodeWithTag(ProductListContentTestTags.SCROLL_TO_TOP_FAB)
            .assertDoesNotExist()
    }

    @Test
    fun scrollToTopFab_hasCorrectContentDescription() {
        setContent(currentPage = 4)

        val description = composeTestRule.activity.getString(R.string.button_scroll_up_a11y)
        composeTestRule
            .onNodeWithContentDescription(description)
            .assertIsDisplayed()
    }

    @Test
    fun scrollToTopFab_click_invokesOnScrollToTop() {
        var scrollToTopCalled = false

        setContent(
            currentPage = 4,
            onScrollToTop = { scrollToTopCalled = true },
        )

        composeTestRule
            .onNodeWithTag(ProductListContentTestTags.SCROLL_TO_TOP_FAB)
            .performClick()

        composeTestRule.waitForIdle()
        assertTrue(scrollToTopCalled)
    }

    @Test
    fun scrollToTopFab_click_doesNotInvokeOnScrollToTop_whenFabHidden() {
        var scrollToTopCalled = false

        setContent(
            currentPage = 1,
            onScrollToTop = { scrollToTopCalled = true },
        )

        composeTestRule
            .onNodeWithTag(ProductListContentTestTags.SCROLL_TO_TOP_FAB)
            .assertDoesNotExist()

        assertFalse(scrollToTopCalled)
    }

    @Test
    fun onLoadNextPage_isInvoked_whenListHasFewerThan10Items() {
        var loadNextPageCallCount = 0

        setContent(
            uiModels = buildUiModels(3),
            onLoadNextPage = { loadNextPageCallCount++ },
        )

        composeTestRule.waitForIdle()
        assertTrue(loadNextPageCallCount > 0)
    }

    private fun setContent(
        uiModels: List<ProductUiModel> = buildUiModels(3),
        currentPage: Int = 1,
        onLoadNextPage: () -> Unit = {},
        onScrollToTop: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            MaterialTheme {
                Box {
                    ProductListContent(
                        uiModels = uiModels,
                        currentPage = currentPage,
                        onLoadNextPage = onLoadNextPage,
                        onScrollToTop = onScrollToTop,
                    )
                }
            }
        }
    }

    private fun buildUiModels(count: Int): List<ProductUiModel> =
        (1..count).map { index ->
            ProductUiModel(
                id = "id_$index",
                name = "Product $index",
                formattedPrice = "$${index * 10}.00",
                imageUrl = "https://img.example.com/$index.jpg",
                imageAltText = "Product $index image",
                swatchDisplayInfo = SwatchDisplayInfo(
                    colors = listOf(Color.Red),
                    overflowText = null,
                ),
            )
        }
}

