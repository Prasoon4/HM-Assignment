package com.example.hmassignment.di

import com.example.hmassignment.data.remote.ApiService
import com.example.hmassignment.data.repository.ProductRepositoryImpl
import com.example.hmassignment.domain.repository.ProductRepository
import com.example.hmassignment.domain.usecase.GetProductsUseCase
import com.example.hmassignment.domain.usecase.GetProductsUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AppModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
    }

    @Provides
    @Singleton
    fun provideApiService(client: HttpClient) = ApiService(client)

    @Provides
    @Singleton
    fun provideProductRepository(apiService: ApiService): ProductRepository {
        return ProductRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideGetProductsUseCase(productRepository: ProductRepository): GetProductsUseCase {
        return GetProductsUseCaseImpl(productRepository)
    }
}