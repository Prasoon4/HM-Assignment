package com.example.hmassignment.presentation.compose

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hmassignment.R
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class ErrorContentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun errorMessage_showsExactText() {
        val message = "Network error"
        setContent(message = message)

        composeTestRule
            .onNodeWithTag(ErrorContentTestTags.ERROR_MESSAGE)
            .assertTextEquals(message)
            .assertIsDisplayed()
    }

    @Test
    fun retryButton_showsCorrectLabelAndDescription() {
        setContent()

        val retryLabel = composeTestRule.activity.getString(R.string.button_retry)
        val retryA11y = composeTestRule.activity.getString(R.string.button_retry_a11y)

        composeTestRule
            .onNodeWithText(retryLabel)
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription(retryA11y)
            .assertIsDisplayed()
    }

    @Test
    fun retryButton_click_invokesOnRetryCallback() {
        var retryCalled = false

        setContent(onRetry = { retryCalled = true })

        composeTestRule
            .onNodeWithTag(ErrorContentTestTags.RETRY_BUTTON)
            .performClick()

        assertTrue(retryCalled)
    }

   private fun setContent(
        message: String = "Something went wrong",
        onRetry: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            MaterialTheme {
                ErrorContent(
                    message = message,
                    onRetry = onRetry,
                )
            }
        }
    }
}
