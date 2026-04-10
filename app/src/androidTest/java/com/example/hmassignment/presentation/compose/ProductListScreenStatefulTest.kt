package com.example.hmassignment.presentation.compose

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hmassignment.presentation.ProductUiModel
import com.example.hmassignment.presentation.SwatchDisplayInfo
import com.example.hmassignment.presentation.viewmodel.ProductViewModel.ProductEvent
import com.example.hmassignment.presentation.viewmodel.ProductViewModel.ProductState
import com.example.hmassignment.presentation.viewmodel.ProductViewModel.ProductUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class ProductListScreenStatefulTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun initialLoading_showsProgressIndicator() {
        val state = ProductState(uiState = ProductUiState.InitialLoading)
        setContent(state)

        composeTestRule
            .onNodeWithTag(ProductListScreenTestTags.INITIAL_LOADING)
            .assertIsDisplayed()
    }

    @Test
    fun errorState_showsErrorContent() {
        val state = ProductState(uiState = ProductUiState.Error(""))
        setContent(state)

        composeTestRule
            .onNodeWithTag(ProductListScreenTestTags.ERROR_CONTENT)
            .assertIsDisplayed()
    }

    @Test
    fun successState_emptyList_showsEmptyMessage() {
        val state = ProductState(uiState = ProductUiState.Success(emptyList()))
        setContent(state)

        composeTestRule
            .onNodeWithTag(ProductListScreenTestTags.EMPTY_CONTENT)
            .assertIsDisplayed()
    }

    @Test
    fun successState_withProducts_showsProductList() {
        val products = listOf(
            ProductUiModel(
                id = "1",
                name = "Product 1",
                formattedPrice = "$10",
                imageUrl = "",
                imageAltText = "",
                swatchDisplayInfo = SwatchDisplayInfo(emptyList(), null)
            )
        )
        val state = ProductState(uiState = ProductUiState.Success(products))
        setContent(state)

        composeTestRule
            .onNodeWithTag(ProductListContentTestTags.PRODUCT_LIST_GRID)
            .assertIsDisplayed()
    }

    private fun setContent(
        state: ProductState,
        onEvent: (ProductEvent) -> Unit = {}
    ) {
        composeTestRule.setContent {
            ProductListScreenStateful(
                state = state,
                onEvent = onEvent
            )
        }
    }
}
