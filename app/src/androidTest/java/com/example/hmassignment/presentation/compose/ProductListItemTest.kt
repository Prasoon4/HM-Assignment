package com.example.hmassignment.presentation.compose

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hmassignment.presentation.ProductUiModel
import com.example.hmassignment.presentation.SwatchDisplayInfo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class ProductListItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun productName_isDisplayed() {
        val name = "Slim Jeans"
        setContent(name = name)

        composeTestRule
            .onNodeWithTag(ProductListItemTestTags.PRODUCT_NAME)
            .assertExists()
            .assertTextEquals(name)
    }

    @Test
    fun price_isDisplayed_whenNotNull() {
        val formattedPrice = "$49.99"
        setContent(formattedPrice = formattedPrice)

        composeTestRule
            .onNodeWithTag(ProductListItemTestTags.PRODUCT_PRICE)
            .assertExists()
            .assertTextEquals(formattedPrice)
    }

    @Test
    fun price_doesNotExist_whenNull() {
        setContent(formattedPrice = null)

        composeTestRule
            .onNodeWithTag(ProductListItemTestTags.PRODUCT_PRICE)
            .assertDoesNotExist()
    }

    @Test
    fun image_nodeExists_withTestTag() {
        setContent()

        composeTestRule
            .onNodeWithTag(ProductListItemTestTags.PRODUCT_IMAGE)
            .assertExists()
    }

    @Test
    fun image_hasCorrectContentDescription() {
        val imageAltText = "Blue Jeans product photo"
        setContent(imageAltText = imageAltText)

        composeTestRule
            .onNodeWithContentDescription(imageAltText)
            .assertExists()
    }

    @Test
    fun swatchDots_displaysCorrectCount_forSingleSwatch() {
        setContent(swatchColors = listOf(Color.Red))

        composeTestRule
            .onAllNodesWithTag(ProductListItemTestTags.SWATCH_DOT)
            .assertCountEquals(1)
    }

    @Test
    fun swatchDots_displaysCorrectCount_forThreeSwatches() {
        setContent(swatchColors = listOf(Color.Red, Color.Green, Color.Blue))

        composeTestRule
            .onAllNodesWithTag(ProductListItemTestTags.SWATCH_DOT)
            .assertCountEquals(3)
    }

    @Test
    fun swatchDots_countIsZero_whenSwatchListIsEmpty() {
        setContent(swatchColors = emptyList())

        composeTestRule
            .onAllNodesWithTag(ProductListItemTestTags.SWATCH_DOT)
            .assertCountEquals(0)
    }

    @Test
    fun overflowText_isDisplayed_withCorrectValue() {
        val overflowText = "+2"
        setContent(
            swatchColors = listOf(Color.Red, Color.Green, Color.Blue),
            overflowText = overflowText,
        )

        composeTestRule
            .onNodeWithTag(ProductListItemTestTags.SWATCH_OVERFLOW_TEXT)
            .assertExists()
            .assertTextEquals(overflowText)
    }

    @Test
    fun overflowText_doesNotExist_whenNull() {
        setContent(
            swatchColors = listOf(Color.Red, Color.Green),
            overflowText = null,
        )

        composeTestRule
            .onNodeWithTag(ProductListItemTestTags.SWATCH_OVERFLOW_TEXT)
            .assertDoesNotExist()
    }

    private fun setContent(
        id: String = "1",
        name: String = "Default Jeans",
        formattedPrice: String? = "$29.99",
        imageUrl: String = "https://img.example.com/1.jpg",
        imageAltText: String = "Product image",
        swatchColors: List<Color> = emptyList(),
        overflowText: String? = null,
    ) {
        composeTestRule.setContent {
            MaterialTheme {
                ProductListItem(
                    uiModel = ProductUiModel(
                        id = id,
                        name = name,
                        formattedPrice = formattedPrice,
                        imageUrl = imageUrl,
                        imageAltText = imageAltText,
                        swatchDisplayInfo = SwatchDisplayInfo(
                            colors = swatchColors,
                            overflowText = overflowText,
                        ),
                    )
                )
            }
        }
    }
}
