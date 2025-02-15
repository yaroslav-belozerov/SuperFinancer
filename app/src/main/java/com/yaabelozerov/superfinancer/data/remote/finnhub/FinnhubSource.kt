package com.yaabelozerov.superfinancer.data.remote.finnhub

import com.yaabelozerov.superfinancer.BuildConfig
import com.yaabelozerov.superfinancer.data.remote.Net
import com.yaabelozerov.superfinancer.data.remote.finnhub.ticker.ProfileDto
import com.yaabelozerov.superfinancer.data.remote.finnhub.ticker.TickerDto
import com.yaabelozerov.superfinancer.data.remote.finnhub.ticker.TickerReceiveConnection
import com.yaabelozerov.superfinancer.data.remote.finnhub.ticker.TickerSearchDto
import com.yaabelozerov.superfinancer.data.remote.finnhub.ticker.TickerStartConnection
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.client.plugins.websocket.wss
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.headers
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json

class FinnhubSource(private val client: HttpClient = Net.Client) {

    suspend fun getRateForSymbol(
        symbol: String,
        token: String = BuildConfig.FINNHUB_TOKEN,
    ): Result<TickerDto> = runCatching {
        client.get {
            url("${BASE_URL}quote?symbol=$symbol")
            headers {
                header("X-Finnhub-Token", token)
            }
        }.body<TickerDto>()
    }

    suspend fun getInfoForSymbol(
        symbol: String,
        token: String = BuildConfig.FINNHUB_TOKEN,
    ): Result<ProfileDto> = runCatching {
        client.get {
            url("${BASE_URL}stock/profile2?symbol=$symbol")
            headers { header("X-Finnhub-Token", token) }
        }.body()
    }

    suspend fun searchSymbol(query: String, token: String = BuildConfig.FINNHUB_TOKEN): Result<TickerSearchDto> = kotlin.runCatching {
        client.get {
            url("${BASE_URL}search?q=$query")
            headers { header("X-Finnhub-Token", token) }
        }.body()
    }

    suspend fun startTickerConnection(symbol: String, onReceive: suspend (TickerReceiveConnection) -> Unit, onError: (Throwable) -> Unit) {
        try {
            client.wss("$BASE_URL_WS?token=${BuildConfig.FINNHUB_TOKEN}") {
                sendSerialized(TickerStartConnection(symbol = symbol))
                incoming.consumeEach {
                    when (it) {
                        is Frame.Close -> return@wss
                        is Frame.Text -> {
                            onReceive(Json.decodeFromString<TickerReceiveConnection>(it.readText()))
                        }
                        else -> Unit
                    }
                }
            }
        } catch (t: Throwable) { onError(t) }
    }

    companion object {
        private const val BASE_URL = "https://finnhub.io/api/v1/"
        private const val BASE_URL_WS = "wss://ws.finnhub.io/"
    }
}