package com.yaabelozerov.superfinancer.tickers.data

import kotlinx.serialization.Serializable

@Serializable
internal data class TickerSearchDto(
    val count: Long,
    val result: List<TickerSearchResultDto>
)

@Serializable
internal data class TickerSearchResultDto(
    val description: String,
    val displaySymbol: String,
    val symbol: String,
    val type: String
)