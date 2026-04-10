package com.example.hmassignment.domain.repository

import com.example.hmassignment.domain.model.ProductResponse

internal interface ProductRepository {
    suspend fun getProducts(page: Int): Result<ProductResponse>
}