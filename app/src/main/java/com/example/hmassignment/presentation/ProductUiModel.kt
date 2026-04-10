package com.example.hmassignment.presentation

import androidx.compose.ui.graphics.Color

internal data class ProductUiModel(
    val id: String,
    val name: String,
    val formattedPrice: String?,
    val imageUrl: String,
    val imageAltText: String,
    val swatchDisplayInfo: SwatchDisplayInfo,
)

internal data class SwatchDisplayInfo(
    val colors: List<Color>,
    val overflowText: String?,
)