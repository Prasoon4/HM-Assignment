package com.example.hmassignment.domain.model

internal data class ProductResponse(
    val products: List<Product>,
    val pagination: Pagination,
)

internal data class Product(
    val id: String,
    val name: String,
    val formattedPrice: String?,
    val imageInfo: ImageInfo,
    val swatches: List<Swatch>
)

internal data class Pagination(
    val currentPage: Int,
    val nextPage: Int?,
    val totalPages: Int
)

internal data class ImageInfo(
    val url: String,
    val altText: String,
)

internal data class Swatch(
    val colorCode: String,
)
