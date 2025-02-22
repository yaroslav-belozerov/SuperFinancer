package com.yaabelozerov.superfinancer.tickers.data

import kotlinx.serialization.Serializable

@Serializable
data class EarningsDto(
    val actual: Double,
    val estimate: Double,
    val period: String,
    val quarter: Long,
    val surprise: Double,
    val surprisePercent: Double,
    val symbol: String,
    val year: Long,
)
