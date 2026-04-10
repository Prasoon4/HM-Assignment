package com.example.hmassignment.presentation.compose

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hmassignment.R

@Composable
internal fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val retryLabel = stringResource(R.string.button_retry)
    val retryA11y  = stringResource(R.string.button_retry_a11y)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            modifier = Modifier.testTag(ErrorContentTestTags.ERROR_MESSAGE),
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier
                .testTag(ErrorContentTestTags.RETRY_BUTTON)
                .semantics { contentDescription = retryA11y }
        ) {
            Text(retryLabel)
        }
    }
}

@Preview
@Composable
private fun PreviewErrorContent() {
    ErrorContent(
        message = "Something went wrong",
        onRetry = {}
    )
}

@VisibleForTesting
internal object ErrorContentTestTags {
    const val ERROR_MESSAGE = "error_message"
    const val RETRY_BUTTON  = "retry_button"
}
