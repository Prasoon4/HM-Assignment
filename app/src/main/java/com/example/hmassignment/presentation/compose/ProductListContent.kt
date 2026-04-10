package com.example.hmassignment.presentation.compose

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.annotation.VisibleForTesting
import androidx.compose.ui.res.stringResource
import com.example.hmassignment.R
import com.example.hmassignment.presentation.ProductUiModel
import kotlinx.coroutines.launch

@Composable
internal fun BoxScope.ProductListContent(
    uiModels: List<ProductUiModel>,
    currentPage: Int,
    onLoadNextPage: () -> Unit,
    onScrollToTop: () -> Unit,
) {
    val listState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    val showScrollToTop = currentPage > 3

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = listState,
        modifier = Modifier.testTag(ProductListContentTestTags.PRODUCT_LIST_GRID),
    ) {
        itemsIndexed(uiModels) { index, uiModel ->
            ProductListItem(
                uiModel = uiModel,
                modifier = Modifier.testTag(ProductListContentTestTags.PRODUCT_ITEM),
            )

            val shouldLoadMore by remember {
                derivedStateOf {
                    val lastVisible = listState
                        .layoutInfo
                        .visibleItemsInfo
                        .lastOrNull()
                        ?.index ?: 0
                    lastVisible >= uiModels.size - 10
                }
            }
            LaunchedEffect(shouldLoadMore) {
                if (shouldLoadMore) onLoadNextPage()
            }
        }
    }

    if (showScrollToTop) {
        val buttonA11y = stringResource(R.string.button_scroll_up_a11y)
        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    listState.scrollToItem(0)
                    onScrollToTop()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag(ProductListContentTestTags.SCROLL_TO_TOP_FAB)
                .semantics {
                    contentDescription = buttonA11y
                    role = Role.Button
                }
        ) {
            Text(text = "↑", fontSize = 20.sp)
        }
    }
}

@VisibleForTesting
internal object ProductListContentTestTags {
    const val PRODUCT_ITEM = "product_item"
    const val PRODUCT_LIST_GRID = "product_list_grid"
    const val SCROLL_TO_TOP_FAB = "scroll_to_top_fab"
}

