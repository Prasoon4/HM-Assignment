package com.example.hmassignment.presentation.compose

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hmassignment.presentation.viewmodel.ProductViewModel

@Composable
internal fun ProductListScreen(viewModel: ProductViewModel = hiltViewModel()) {

    ProductListScreenStateful(
        state = viewModel.state,
        onEvent = viewModel::onEvent
    )
}
