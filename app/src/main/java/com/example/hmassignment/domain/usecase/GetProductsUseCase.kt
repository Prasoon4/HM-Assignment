package com.example.hmassignment.domain.usecase

import com.example.hmassignment.domain.model.ProductResponse
import com.example.hmassignment.domain.repository.ProductRepository
internal interface GetProductsUseCase {
    suspend operator fun invoke(page: Int): Result<ProductResponse>
}

internal class GetProductsUseCaseImpl(
    private val repository: ProductRepository
): GetProductsUseCase {
    override suspend operator fun invoke(page: Int) =
        repository.getProducts(page)
}