package com.yaabelozerov.superfinancer.domain.usecase

import com.yaabelozerov.superfinancer.data.remote.finnhub.FinnhubSource
import com.yaabelozerov.superfinancer.data.remote.finnhub.profile.ProfileDto
import com.yaabelozerov.superfinancer.data.remote.finnhub.ticker.TickerDto
import com.yaabelozerov.superfinancer.domain.model.Ticker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

private infix fun TickerDto.combine(info: ProfileDto): Ticker {
    return Ticker(
        value = this.current.toString(),
        name = info.name,
        currency = info.currency,
        logoUrl = info.logo,
        changePercent = this.deltaPercent
    )
}

private fun TickerDto.isValid(): Boolean {
    return current != 0.0 && high != 0.0 && low != 0.0 && open != 0.0 && previousOpen != 0.0 && deltaPercent != null && delta != null
}

class TickerUseCase(private val source: FinnhubSource = FinnhubSource()) {
    fun tickerFlow(symbols: List<String>): Flow<Triple<String, Ticker?, Throwable?>> = flow {
        symbols.forEach { symbol ->
            val rate = source.getRateForSymbol(symbol);
            val rateContent = rate.getOrNull()
            if (rateContent?.isValid() == false) {
                emit(
                    Triple(
                        symbol,
                        null,
                        Throwable("No ticker for symbol $symbol")
                    )
                )
                return@forEach
            }
            val info = source.getInfoForSymbol(symbol);
            val infoContent = info.getOrNull()
            if (rateContent == null || infoContent == null) {
                emit(
                    Triple(
                        symbol,
                        null,
                        (rate.exceptionOrNull() ?: info.exceptionOrNull()
                        ?: Throwable("Unknown error"))
                    )
                )
            } else {
                emit(Triple(symbol, rateContent combine infoContent, null))
            }
        }
    }.flowOn(Dispatchers.IO)
}