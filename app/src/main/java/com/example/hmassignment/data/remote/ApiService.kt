package com.example.hmassignment.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

private const val URL = "https://api.hm.com/search-services/v1/sv_se/search/resultpage"

internal class ApiService(private val client: HttpClient) {

    suspend fun getProducts(page: Int): ProductResponseRemote {
        return client.get(URL) {
            parameter("touchPoint", "android")
            parameter("query", "jeans")
            parameter("page", page)
        }.body()
    }
}