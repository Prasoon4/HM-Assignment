package com.example.hmassignment.data.remote

import kotlinx.serialization.Serializable

@Serializable
internal data class ProductResponseRemote(
    val pagination: PaginationRemote,
    val searchHits: SearchHitsRemote,
)

@Serializable
internal data class PaginationRemote(
    val currentPage: Int,
    val nextPageNum: Int?,
    val totalPages: Int
)

@Serializable
internal data class SearchHitsRemote(
    val productList: List<ProductRemote>,
)

@Serializable
internal data class ProductRemote(
    val id: String,
    val productName: String,
    val productImage: String,
    val productImageInfo: ImageInfoRemote? = null,
    val swatches: List<SwatchRemote>,
    val prices: List<PriceRemote>,
)

@Serializable
internal data class ImageInfoRemote(
    val url: String,
    val altText: String,
)

@Serializable
internal data class PriceRemote(
    val formattedPrice: String,
)

@Serializable
internal data class SwatchRemote(
    val colorCode: String,
)
