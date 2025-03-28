package com.yaabelozerov.superfinancer.tickers.domain

import android.content.Context
import com.yaabelozerov.superfinancer.common.CommonModule
import com.yaabelozerov.superfinancer.common.util.toString
import com.yaabelozerov.superfinancer.tickers.TickerModule
import com.yaabelozerov.superfinancer.tickers.data.FinnhubSource
import com.yaabelozerov.superfinancer.tickers.data.ProfileDto
import com.yaabelozerov.superfinancer.tickers.data.TickerDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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

internal class TickerUseCase(
    val context: Context = TickerModule.context,
    private val source: FinnhubSource = FinnhubSource(),
) {
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

    private val _tickerConnectionFlow = MutableSharedFlow<Pair<String, Double>>(replay = 1)
    val tickerConnectionFlow = _tickerConnectionFlow.asSharedFlow()

    suspend fun startConnectionsForTickers(tickers: List<String>) {
        coroutineScope {
            tickers.forEach { symbol ->
                delay(200L)
                launch {
                    source.startTickerConnection(symbol, onReceive = {
                        it.data.map {
                            _tickerConnectionFlow.emit(it.symbol to it.price)
                        }
                    }, onError = { System.err.println(it.message) })
                }
            }
        }
    }

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        val defaultTickers = CommonModule.config.defaultTickers
    }
}