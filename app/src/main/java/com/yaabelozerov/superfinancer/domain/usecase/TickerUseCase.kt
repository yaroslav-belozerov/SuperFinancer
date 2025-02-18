package com.yaabelozerov.superfinancer.domain.usecase

import com.yaabelozerov.superfinancer.data.remote.finnhub.FinnhubSource
import com.yaabelozerov.superfinancer.data.remote.finnhub.ticker.ProfileDto
import com.yaabelozerov.superfinancer.data.remote.finnhub.ticker.TickerDto
import com.yaabelozerov.superfinancer.domain.model.Ticker
import com.yaabelozerov.superfinancer.ui.toString
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

private infix fun TickerDto.combine(info: ProfileDto): Ticker {
    return Ticker(
        value = this.current.toString(2),
        name = info.name,
        currency = info.currency,
        logoUrl = info.logo,
        changePercent = this.deltaPercent
    )
}

class TickerUseCase(private val source: FinnhubSource = FinnhubSource()) {
    suspend fun getFullInfoForSymbols(symbols: List<String>): Map<String, Ticker> = coroutineScope {
        symbols.map {
            async {
                val rateData = source.getRateForSymbol(it).getOrNull()
                val infoData = source.getInfoForSymbol(it).getOrNull()
                if (infoData != null && rateData != null) {
                    Pair(it, rateData combine infoData)
                } else null
            }
        }
    }.awaitAll().filterNotNull().toMap()

    val tickerConnectionFlow = MutableSharedFlow<Pair<String, Double>>()

    suspend fun startConnectionsForTickers(tickers: List<String>) {
        coroutineScope {
            tickers.forEach { symbol ->
                delay(200L)
                launch {
                    source.startTickerConnection(symbol, onReceive = {
                        it.data.map {
                            tickerConnectionFlow.emit(it.symbol to it.price)
                        }
                    }, onError = { it.printStackTrace() })
                }
            }
        }
    }
}