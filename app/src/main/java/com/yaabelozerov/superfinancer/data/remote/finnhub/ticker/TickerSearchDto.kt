package com.yaabelozerov.superfinancer.data.remote.finnhub.ticker

import kotlinx.serialization.Serializable

@Serializable
data class TickerSearchDto(
    val count: Long,
    val result: List<TickerSearchResultDto>
)

@Serializable
data class TickerSearchResultDto(
    val description: String,
    val displaySymbol: String,
    val symbol: String,
    val type: String
)