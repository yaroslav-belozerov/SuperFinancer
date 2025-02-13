package com.yaabelozerov.superfinancer.data.remote.finnhub

import com.yaabelozerov.superfinancer.BuildConfig
import com.yaabelozerov.superfinancer.data.remote.Net
import com.yaabelozerov.superfinancer.data.remote.profile.ProfileDto
import com.yaabelozerov.superfinancer.data.remote.ticker.TickerDto
import com.yaabelozerov.superfinancer.data.remote.ticker.isValid
import com.yaabelozerov.superfinancer.domain.model.Ticker
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.headers

class FinnhubSource(private val client: HttpClient = Net.Client) {

    suspend fun getRateForSymbol(symbol: String, token: String = BuildConfig.FINNHUB_TOKEN): Result<TickerDto> = runCatching {
        client.get {
            url("${BASE_URL}quote?symbol=$symbol")
            headers {
                header("X-Finnhub-Token", token)
            }
        }.body<TickerDto>()
            .also { require(it.isValid()) { "Ticker with symbol $symbol does not exist or is not accessible" } }
    }

    suspend fun getInfoForSymbol(symbol: String, token: String = BuildConfig.FINNHUB_TOKEN): Result<ProfileDto> = runCatching {
        client.get {
            url("${BASE_URL}stock/profile2?symbol=$symbol")
            headers { header("X-Finnhub-Token", token) }
        }.body()
    }

    companion object {
        private const val BASE_URL = "https://finnhub.io/api/v1/"
    }
}