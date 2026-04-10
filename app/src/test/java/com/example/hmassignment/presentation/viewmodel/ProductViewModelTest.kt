package com.example.hmassignment.presentation.viewmodel

import com.example.hmassignment.domain.model.ImageInfo
import com.example.hmassignment.domain.model.Pagination
import com.example.hmassignment.domain.model.Product
import com.example.hmassignment.domain.model.ProductResponse
import com.example.hmassignment.domain.model.Swatch
import com.example.hmassignment.domain.usecase.GetProductsUseCase
import com.example.hmassignment.presentation.viewmodel.ProductViewModel.ProductEvent
import com.example.hmassignment.presentation.viewmodel.ProductViewModel.ProductUiState
import com.example.hmassignment.util.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
internal class ProductViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val getProducts: GetProductsUseCase = mockk()

    @Test
    fun `state is InitialLoading before first load completes`() = coroutineRule.runTest {
        coEvery {
            getProducts(any())
        } coAnswers { delay(Long.MAX_VALUE); Result.success(buildResponse()) }

        val viewModel = createViewModel()
        assertEquals(ProductUiState.InitialLoading, viewModel.state.uiState)
        assertFalse(viewModel.state.isLoading)
    }

    @Test
    fun `default state has page 1 and isEndReached false`() = coroutineRule.runTest {
        coEvery {
            getProducts(any())
        } coAnswers { delay(Long.MAX_VALUE); Result.success(buildResponse()) }

        val viewModel = createViewModel()
        assertEquals(1, viewModel.state.currentPage)
        assertFalse(viewModel.state.isEndReached)
    }

    @Test
    fun `isLoading is true while request is in flight`() = coroutineRule.runTest {
        coEvery {
            getProducts(1)
        } coAnswers { delay(1_000); Result.success(buildResponse()) }

        val viewModel = createViewModel()
        runCurrent()

        assertTrue(viewModel.state.isLoading)
    }

    @Test
    fun `isLoading is false after successful response`() = coroutineRule.runTest {
        coEvery { getProducts(1) } returns Result.success(buildResponse())

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.state.isLoading)
    }

    @Test
    fun `isLoading is false after error response`() = coroutineRule.runTest {
        coEvery { getProducts(1) } returns Result.failure(RuntimeException())

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.state.isLoading)
    }

    @Test
    fun `second loadProducts call is ignored while first is still in flight`() = coroutineRule
        .runTest {
            coEvery {
                getProducts(any())
            } coAnswers { delay(1_000); Result.success(buildResponse()) }

            val viewModel = createViewModel()
            runCurrent()

            viewModel.onEvent(ProductEvent.LoadNextPage)
            advanceUntilIdle()

            coVerify(exactly = 1) { getProducts(any()) }
        }

    @Test
    fun `state becomes Success after init load`() = coroutineRule.runTest {
        coEvery { getProducts(1) } returns Result.success(buildResponse())

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.state.uiState is ProductUiState.Success)
    }

    @Test
    fun `state becomes Error when init load fails`() = coroutineRule.runTest {
        coEvery {
            getProducts(1)
        } returns Result.failure(RuntimeException("Network error"))

        val viewModel = createViewModel()
        advanceUntilIdle()

        val errorState = viewModel.state.uiState as ProductUiState.Error
        assertEquals("Network error", errorState.message)
    }

    @Test
    fun `product fields are mapped correctly to UiModel`() = coroutineRule.runTest {
        val product = buildProduct(
            id = "42",
            name = "Slim Jeans",
            formattedPrice = "$49.99",
            imageUrl = "https://img.example.com/1.jpg",
            imageAltText = "Slim Jeans alt",
        )
        coEvery { getProducts(1) } returns Result.success(buildResponse(products = listOf(product)))

        val viewModel = createViewModel()
        advanceUntilIdle()

        val ui = successProducts(viewModel)[0]
        assertEquals("42", ui.id)
        assertEquals("Slim Jeans", ui.name)
        assertEquals("$49.99", ui.formattedPrice)
        assertEquals("https://img.example.com/1.jpg", ui.imageUrl)
        assertEquals("Slim Jeans alt", ui.imageAltText)
    }

    @Test
    fun `swatches beyond 3 are trimmed and overflow count is shown`() = coroutineRule
        .runTest {
            val product = buildProduct(
                swatches = listOf(
                    Swatch("FF0000"), Swatch("00FF00"), Swatch("0000FF"),
                    Swatch("FFFFFF"), Swatch("000000"),
                )
            )
            coEvery {
                getProducts(1)
            } returns Result.success(buildResponse(products = listOf(product)))

            val viewModel = createViewModel()
            advanceUntilIdle()

            val info = successProducts(viewModel)[0].swatchDisplayInfo
            assertEquals(3, info.colors.size)
            assertEquals("+2", info.overflowText)
        }

    @Test
    fun `overflow text is null when swatches are within the 3-item limit`() = coroutineRule
        .runTest {
            val product = buildProduct(
                swatches = listOf(Swatch("FF0000"), Swatch("00FF00")),
            )
            coEvery {
                getProducts(1)
            } returns Result.success(buildResponse(products = listOf(product)))

            val viewModel = createViewModel()
            advanceUntilIdle()

            assertNull(successProducts(viewModel)[0].swatchDisplayInfo.overflowText)
        }

    @Test
    fun `invalid swatch color codes are filtered out`() = coroutineRule.runTest {
        val product = buildProduct(
            swatches = listOf(Swatch("FF0000"), Swatch("INVALID"), Swatch("0000FF"))
        )
        coEvery {
            getProducts(1)
        } returns Result.success(buildResponse(products = listOf(product)))

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(2, successProducts(viewModel)[0].swatchDisplayInfo.colors.size)
    }

    @Test
    fun `empty swatches list produces empty SwatchDisplayInfo`() = coroutineRule.runTest {
        val product = buildProduct(swatches = emptyList())
        coEvery {
            getProducts(1)
        } returns Result.success(buildResponse(products = listOf(product)))

        val viewModel = createViewModel()
        advanceUntilIdle()

        val info = successProducts(viewModel)[0].swatchDisplayInfo
        assertTrue(info.colors.isEmpty())
        assertNull(info.overflowText)
    }

    @Test
    fun `currentPage is updated to nextPage after successful load`() = coroutineRule.runTest {
        coEvery { getProducts(1) } returns Result.success(
            buildResponse(currentPage = 1, nextPage = 2, totalPages = 5)
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(2, viewModel.state.currentPage)
    }

    @Test
    fun `currentPage stays at currentPage when nextPage is null`() = coroutineRule.runTest {
        coEvery { getProducts(1) } returns Result.success(
            buildResponse(currentPage = 3, nextPage = null, totalPages = 3)
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(3, viewModel.state.currentPage)
    }

    @Test
    fun `isEndReached is true when currentPage equals totalPages`() = coroutineRule.runTest {
        coEvery { getProducts(1) } returns Result.success(
            buildResponse(currentPage = 5, nextPage = null, totalPages = 5)
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.state.isEndReached)
    }

    @Test
    fun `isEndReached is false when currentPage is less than totalPages`() = coroutineRule.runTest {
        coEvery { getProducts(1) } returns Result.success(
            buildResponse(currentPage = 1, nextPage = 2, totalPages = 5)
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.state.isEndReached)
    }

    @Test
    fun `LoadNextPage appends page 2 products to the existing list`() = coroutineRule.runTest {
        coEvery { getProducts(1) } returns Result.success(
            buildResponse(
                products = listOf(buildProduct(id = "p1")),
                currentPage = 1,
                nextPage = 2,
                totalPages = 3,
            )
        )
        coEvery { getProducts(2) } returns Result.success(
            buildResponse(
                products = listOf(buildProduct(id = "p2")),
                currentPage = 2,
                nextPage = 3,
                totalPages = 3,
            )
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(ProductEvent.LoadNextPage)
        advanceUntilIdle()

        val products = successProducts(viewModel)
        assertEquals(2, products.size)
        assertEquals("p1", products[0].id)
        assertEquals("p2", products[1].id)
    }

    @Test
    fun `LoadNextPage does nothing when isEndReached is true`() = coroutineRule.runTest {
        coEvery { getProducts(1) } returns Result.success(
            buildResponse(currentPage = 5, nextPage = null, totalPages = 5)
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onEvent(ProductEvent.LoadNextPage)
        advanceUntilIdle()

        coVerify(exactly = 1) { getProducts(any()) }
    }

    @Test
    fun `Retry recovers from error and shows success state`() = coroutineRule.runTest {
        coEvery {
            getProducts(1)
        } returns Result.failure(RuntimeException("Error"))

        val viewModel = createViewModel()
        advanceUntilIdle()
        assertTrue(viewModel.state.uiState is ProductUiState.Error)

        coEvery { getProducts(1) } returns Result.success(buildResponse())
        viewModel.onEvent(ProductEvent.Retry)
        advanceUntilIdle()

        assertTrue(viewModel.state.uiState is ProductUiState.Success)
    }

    @Test
    fun `ScrollToTop replaces the product list with a fresh page 1 load`() = coroutineRule.runTest {
        coEvery { getProducts(1) } returns Result.success(
            buildResponse(
                products = listOf(buildProduct(id = "old")),
                currentPage = 1,
                nextPage = 2,
                totalPages = 3,
            )
        )
        val viewModel = createViewModel()
        advanceUntilIdle()

        coEvery { getProducts(1) } returns Result.success(
            buildResponse(
                products = listOf(buildProduct(id = "new")),
                currentPage = 1,
                nextPage = 2,
                totalPages = 3,
            )
        )
        viewModel.onEvent(ProductEvent.ScrollToTop)
        advanceUntilIdle()

        val products = successProducts(viewModel)
        assertEquals(1, products.size)
        assertEquals("new", products[0].id)
    }

    private fun createViewModel() = ProductViewModel(getProducts)

    private fun successProducts(vm: ProductViewModel) =
        (vm.state.uiState as ProductUiState.Success).products

    private fun buildResponse(
        products: List<Product> = listOf(buildProduct()),
        currentPage: Int = 1,
        nextPage: Int? = 2,
        totalPages: Int = 5,
    ) = ProductResponse(
        products = products,
        pagination = Pagination(
            currentPage = currentPage,
            nextPage = nextPage,
            totalPages = totalPages,
        ),
    )

    private fun buildProduct(
        id: String = "1",
        name: String = "Default Jeans",
        formattedPrice: String? = "$29.99",
        imageUrl: String = "img.jpg",
        imageAltText: String = "Product image",
        swatches: List<Swatch> = emptyList(),
    ) = Product(
        id = id,
        name = name,
        formattedPrice = formattedPrice,
        imageInfo = ImageInfo(url = imageUrl, altText = imageAltText),
        swatches = swatches,
    )
}
