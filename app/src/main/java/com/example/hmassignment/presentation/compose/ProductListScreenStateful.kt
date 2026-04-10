package com.example.hmassignment.presentation.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.example.hmassignment.R
import com.example.hmassignment.presentation.viewmodel.ProductViewModel
import com.example.hmassignment.presentation.viewmodel.ProductViewModel.ProductEvent
import com.example.hmassignment.presentation.viewmodel.ProductViewModel.ProductUiState

@Composable
internal fun ProductListScreenStateful(
    state: ProductViewModel.ProductState,
    onEvent: (ProductEvent) -> Unit,
) {

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val uiState = state.uiState) {
                ProductUiState.InitialLoading -> InitialLoadingContent(
                    modifier = Modifier.testTag(ProductListScreenTestTags.INITIAL_LOADING),
                )

                is ProductUiState.Error -> ErrorContent(
                    message = uiState.message,
                    onRetry = { onEvent(ProductEvent.Retry) },
                    modifier = Modifier.testTag(ProductListScreenTestTags.ERROR_CONTENT),
                )

                is ProductUiState.Success -> {
                    if (uiState.products.isEmpty()) {
                        EmptyContent(
                            modifier = Modifier.testTag(ProductListScreenTestTags.EMPTY_CONTENT),
                        )
                    } else {
                        ProductListContent(
                            uiModels = uiState.products,
                            currentPage = state.currentPage,
                            onLoadNextPage = { onEvent(ProductEvent.LoadNextPage) },
                            onScrollToTop = { onEvent(ProductEvent.ScrollToTop) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxScope.InitialLoadingContent(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier.align(Alignment.Center),
    )
}

@Composable
private fun BoxScope.EmptyContent(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.no_products_message),
        modifier = modifier.align(Alignment.Center),
    )
}

internal object ProductListScreenTestTags {
    const val INITIAL_LOADING = "initial_loading"
    const val EMPTY_CONTENT = "empty_content"
    const val ERROR_CONTENT = "error_content"
}
