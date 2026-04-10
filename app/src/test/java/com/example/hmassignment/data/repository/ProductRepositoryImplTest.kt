package com.example.hmassignment.data.repository

import com.example.hmassignment.data.remote.ApiService
import com.example.hmassignment.data.remote.ImageInfoRemote
import com.example.hmassignment.data.remote.PaginationRemote
import com.example.hmassignment.data.remote.PriceRemote
import com.example.hmassignment.data.remote.ProductRemote
import com.example.hmassignment.data.remote.ProductResponseRemote
import com.example.hmassignment.data.remote.SearchHitsRemote
import com.example.hmassignment.data.remote.SwatchRemote
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ProductRepositoryImplTest {

    private val apiService: ApiService = mockk()
    private lateinit var repository: ProductRepositoryImpl

    @BeforeEach
    fun setUp() {
        repository = ProductRepositoryImpl(apiService)
    }

    @Test
    fun `getProducts uses productImageInfo url and altText when productImageInfo is present`() = runTest {
        val remote = buildRemoteResponse(
            products = listOf(
                buildProductRemote(
                    productImage = "fallback.jpg",
                    productImageInfo = ImageInfoRemote(url = "info.jpg", altText = "Alt Text"),
                )
            )
        )
        coEvery { apiService.getProducts(1) } returns remote

        val result = repository.getProducts(page = 1)

        assertTrue(result.isSuccess)
        with(result.getOrThrow().products[0].imageInfo) {
            assertEquals("info.jpg", url)
            assertEquals("Alt Text", altText)
        }
    }

    @Test
    fun `getProducts falls back to productImage and productName when productImageInfo is null`() = runTest {
        val remote = buildRemoteResponse(
            products = listOf(
                buildProductRemote(
                    productName = "Black Jeans",
                    productImage = "fallback.jpg",
                    productImageInfo = null,
                )
            )
        )
        coEvery { apiService.getProducts(1) } returns remote

        val result = repository.getProducts(page = 1)

        assertTrue(result.isSuccess)
        with(result.getOrThrow().products[0].imageInfo) {
            assertEquals("fallback.jpg", url)
            assertEquals("Black Jeans", altText)
        }
    }

    @Test
    fun `getProducts maps product fields correctly`() = runTest {
        val remote = buildRemoteResponse(
            products = listOf(
                buildProductRemote(
                    id = "42",
                    productName = "Slim Jeans",
                    prices = listOf(PriceRemote(formattedPrice = "$49.99")),
                    swatches = listOf(SwatchRemote(colorCode = "#0000FF")),
                )
            )
        )
        coEvery { apiService.getProducts(1) } returns remote

        val result = repository.getProducts(page = 1)

        assertTrue(result.isSuccess)
        with(result.getOrThrow().products[0]) {
            assertEquals("42", id)
            assertEquals("Slim Jeans", name)
            assertEquals("$49.99", formattedPrice)
            assertEquals(1, swatches.size)
            assertEquals("#0000FF", swatches[0].colorCode)
        }
    }

    @Test
    fun `getProducts maps formattedPrice to null when prices list is empty`() = runTest {
        val remote = buildRemoteResponse(
            products = listOf(buildProductRemote(prices = emptyList()))
        )
        coEvery { apiService.getProducts(1) } returns remote

        val result = repository.getProducts(page = 1)

        assertTrue(result.isSuccess)
        assertNull(result.getOrThrow().products[0].formattedPrice)
    }

    @Test
    fun `getProducts maps multiple swatches correctly`() = runTest {
        val swatchRemotes = listOf(
            SwatchRemote(colorCode = "#FF0000"),
            SwatchRemote(colorCode = "#00FF00"),
            SwatchRemote(colorCode = "#0000FF"),
        )
        val remote = buildRemoteResponse(
            products = listOf(buildProductRemote(swatches = swatchRemotes))
        )
        coEvery { apiService.getProducts(1) } returns remote

        val result = repository.getProducts(page = 1)

        assertTrue(result.isSuccess)
        val swatches = result.getOrThrow().products[0].swatches
        assertEquals(3, swatches.size)
        assertEquals("#FF0000", swatches[0].colorCode)
        assertEquals("#00FF00", swatches[1].colorCode)
        assertEquals("#0000FF", swatches[2].colorCode)
    }

    @Test
    fun `getProducts returns empty products list when searchHits productList is empty`() = runTest {
        val remote = buildRemoteResponse(products = emptyList())
        coEvery { apiService.getProducts(1) } returns remote

        val result = repository.getProducts(page = 1)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().products.isEmpty())
    }

    @Test
    fun `getProducts maps pagination fields correctly`() = runTest {
        val remote = buildRemoteResponse(currentPage = 2, nextPageNum = 3, totalPages = 10)
        coEvery { apiService.getProducts(2) } returns remote

        val result = repository.getProducts(page = 2)

        assertTrue(result.isSuccess)
        with(result.getOrThrow().pagination) {
            assertEquals(2, currentPage)
            assertEquals(3, nextPage)
            assertEquals(10, totalPages)
        }
    }

    @Test
    fun `getProducts maps nextPage to null when on last page`() = runTest {
        val remote = buildRemoteResponse(currentPage = 5, nextPageNum = null, totalPages = 5)
        coEvery { apiService.getProducts(5) } returns remote

        val result = repository.getProducts(page = 5)

        assertTrue(result.isSuccess)
        assertNull(result.getOrThrow().pagination.nextPage)
    }

    @Test
    fun `getProducts returns failure when ApiService throws an exception`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { apiService.getProducts(1) } throws exception

        val result = repository.getProducts(page = 1)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `getProducts wraps any exception type in Result failure`() = runTest {
        val exception = IllegalStateException("Unexpected state")
        coEvery { apiService.getProducts(1) } throws exception

        val result = repository.getProducts(page = 1)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `getProducts calls ApiService with the correct page number`() = runTest {
        coEvery { apiService.getProducts(3) } returns buildRemoteResponse()

        repository.getProducts(page = 3)

        coVerify(exactly = 1) { apiService.getProducts(3) }
    }

    private fun buildRemoteResponse(
        currentPage: Int = 1,
        nextPageNum: Int? = 2,
        totalPages: Int = 5,
        products: List<ProductRemote> = listOf(buildProductRemote()),
    ) = ProductResponseRemote(
        pagination = PaginationRemote(
            currentPage = currentPage,
            nextPageNum = nextPageNum,
            totalPages = totalPages,
        ),
        searchHits = SearchHitsRemote(productList = products),
    )

    private fun buildProductRemote(
        id: String = "1",
        productName: String = "Default Jeans",
        productImage: String = "default.jpg",
        productImageInfo: ImageInfoRemote? = null,
        swatches: List<SwatchRemote> = emptyList(),
        prices: List<PriceRemote> = listOf(PriceRemote(formattedPrice = "$29.99")),
    ) = ProductRemote(
        id = id,
        productName = productName,
        productImage = productImage,
        productImageInfo = productImageInfo,
        swatches = swatches,
        prices = prices,
    )
}
