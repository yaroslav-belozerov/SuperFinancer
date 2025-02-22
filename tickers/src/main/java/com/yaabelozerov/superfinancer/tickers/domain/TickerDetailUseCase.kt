package com.yaabelozerov.superfinancer.tickers.domain

import com.yaabelozerov.superfinancer.tickers.data.FinnhubSource

internal class TickerDetailUseCase(
    private val source: FinnhubSource = FinnhubSource()
) {
    suspend fun getRecommendations(symbol: String) =
        source.getRecommendations(symbol).also { println(it) }.getOrNull()?.map {
            Recommendation(
                buy = it.buy,
                hold = it.hold,
                sell = it.sell,
                strongBuy = it.strongBuy,
                strongSell = it.strongSell,
                date = it.period
            )
        } ?: emptyList()

    suspend fun getEarnings(symbol: String) = source.getEarnings(symbol).getOrNull()?.associateBy {
        "${it.year} Q${it.quarter}"
    }?.map { (year, dto) ->
        Earning(
            estimate = dto.estimate,
            actual = dto.actual,
            date = year,
            surprisePercent = dto.surprisePercent
        )
    } ?: emptyList()
}