package com.example.hmassignment.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hmassignment.presentation.ext.toSafeColor
import com.example.hmassignment.domain.model.Product
import com.example.hmassignment.domain.model.Swatch
import com.example.hmassignment.domain.usecase.GetProductsUseCase
import com.example.hmassignment.presentation.ProductUiModel
import com.example.hmassignment.presentation.SwatchDisplayInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ProductViewModel @Inject constructor(
    private val getProducts: GetProductsUseCase
) : ViewModel() {

    var state by mutableStateOf(ProductState())
        private set

    init {
        loadProducts()
    }

    fun onEvent(event: ProductEvent) {
        when (event) {
            ProductEvent.LoadNextPage -> if (!state.isEndReached) loadProducts()
            ProductEvent.Retry -> loadProducts()
            ProductEvent.ScrollToTop -> loadProducts(reset = true)
        }
    }

    private fun loadProducts(reset: Boolean = false) {
        if (state.isLoading) return

        val page = if (reset) 1 else state.currentPage

        viewModelScope.launch {
            state = state.copy(isLoading = true)

            getProducts(page)
                .onSuccess { (products, pagination) ->
                    state = state.copy(
                        uiState = resolveSuccessState(newProducts = products, reset = reset),
                        currentPage = pagination.nextPage ?: pagination.currentPage,
                        isEndReached = pagination.currentPage >= pagination.totalPages,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    state = state.copy(
                        isLoading = false,
                        uiState = ProductUiState.Error(error.message ?: "Something went wrong")
                    )
                }
        }
    }

    private fun resolveSuccessState(
        newProducts: List<Product>,
        reset: Boolean,
        ): ProductUiState.Success {
        val mapped = newProducts.map { it.toUiModel() }
        return when {
            reset -> ProductUiState.Success(mapped)
            state.uiState is ProductUiState.Success ->
                ProductUiState.Success((state.uiState as ProductUiState.Success).products + mapped)
            else -> ProductUiState.Success(mapped)
        }
    }

    private fun Product.toUiModel() = ProductUiModel(
        id = this.id,
        name = this.name,
        formattedPrice = this.formattedPrice,
        imageUrl = this.imageInfo.url,
        imageAltText = this.imageInfo.altText,
        swatchDisplayInfo = swatches.toSwatchDisplayInfo(),
    )

    private fun List<Swatch>.toSwatchDisplayInfo(): SwatchDisplayInfo {
        val visibleColors = take(MAX_VISIBLE_SWATCHES)
            .mapNotNull { it.colorCode.toSafeColor() }

        val overflowCount = (size - MAX_VISIBLE_SWATCHES).coerceAtLeast(0)

        return SwatchDisplayInfo(
            colors = visibleColors,
            overflowText = if (overflowCount > 0) "+$overflowCount" else null,
        )
    }

    private companion object {
        const val MAX_VISIBLE_SWATCHES = 3
    }

    internal data class ProductState(
        val uiState: ProductUiState = ProductUiState.InitialLoading,
        val currentPage: Int = 1,
        val isEndReached: Boolean = false,
        val isLoading: Boolean = false,
    )

    internal sealed interface ProductUiState {
        data object InitialLoading : ProductUiState
        data class Success(val products: List<ProductUiModel>) : ProductUiState
        data class Error(val message: String) : ProductUiState
    }

    internal sealed interface ProductEvent {
        data object LoadNextPage : ProductEvent
        data object Retry : ProductEvent
        data object ScrollToTop : ProductEvent
    }
}
