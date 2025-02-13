package com.yaabelozerov.superfinancer.data.remote.forex

import com.yaabelozerov.superfinancer.data.remote.Net
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.headers

class ForexSource(private val client: HttpClient = Net.Client) {

    suspend fun getRateForSymbol(symbol: String, token: String): Result<ForexDto> = runCatching {
        client.get {
            url("${BASE_URL}quote?symbol=$symbol")
            headers {
                header("X-Finnhub-Token", token)
            }
        }.body<ForexDto>().also { require(it.isValid()) }
    }

    companion object {
        private const val BASE_URL = "https://finnhub.io/api/v1/"
    }
}