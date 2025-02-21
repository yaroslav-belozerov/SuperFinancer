package com.yaabelozerov.superfinancer.tickers.domain

import com.yaabelozerov.superfinancer.common.CommonModule
import com.yaabelozerov.superfinancer.common.local.config.DataStoreManager
import com.yaabelozerov.superfinancer.common.util.toString
import com.yaabelozerov.superfinancer.tickers.data.FinnhubSource
import com.yaabelozerov.superfinancer.tickers.data.ProfileDto
import com.yaabelozerov.superfinancer.tickers.data.TickerDto
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

private infix fun TickerDto.combine(info: ProfileDto): Ticker {
    return Ticker(
        value = this.current.toString(2),
        name = info.name,
        currency = info.currency,
        logoUrl = info.logo,
        changePercent = this.deltaPercent
    )
}

internal class TickerUseCase(private val source: FinnhubSource = FinnhubSource()) {
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
                delay(500L)
                source.startTickerConnection(symbol, onReceive = {
                    it.data.map {
                        tickerConnectionFlow.emit(it.symbol to it.price)
                    }
                }, onError = { System.err.println(it.message) })
            }
        }
    }

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        val defaultTickers = CommonModule.configManager.readConfig().defaultTickers
    }
}