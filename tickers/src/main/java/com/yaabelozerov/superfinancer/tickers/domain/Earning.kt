package com.yaabelozerov.superfinancer.tickers.domain

data class Earning(
    val estimate: Double,
    val actual: Double,
    val date: String,
    val surprisePercent: Double
)
