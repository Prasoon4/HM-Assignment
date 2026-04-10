package com.example.hmassignment.data.repository

import com.example.hmassignment.data.remote.ApiService
import com.example.hmassignment.domain.model.ImageInfo
import com.example.hmassignment.domain.model.Pagination
import com.example.hmassignment.domain.model.Product
import com.example.hmassignment.domain.model.ProductResponse
import com.example.hmassignment.domain.model.Swatch
import com.example.hmassignment.domain.repository.ProductRepository
import javax.inject.Inject

internal class ProductRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ProductRepository {

    override suspend fun getProducts(page: Int): Result<ProductResponse> {
        return try {
            val response = apiService.getProducts(page)
            val products = response.searchHits.productList.map { product ->

                val imageInfo = ImageInfo(
                    url = product.productImageInfo?.url ?: product.productImage,
                    altText = product.productImageInfo?.altText ?: product.productName
                )

                val swatches = product.swatches.map { swatch ->
                    Swatch(
                        colorCode = swatch.colorCode
                    )
                }

                Product(
                    id = product.id,
                    name = product.productName,
                    formattedPrice = product.prices.firstOrNull()?.formattedPrice,
                    imageInfo = imageInfo,
                    swatches = swatches,
                )
            }
            val pagination = Pagination(
                currentPage = response.pagination.currentPage,
                nextPage = response.pagination.nextPageNum,
                totalPages = response.pagination.totalPages
            )
            Result.success(ProductResponse(products, pagination))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}