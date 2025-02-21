package com.yaabelozerov.superfinancer.tickers.domain

internal data class Ticker(
    val value: String,
    val name: String,
    val currency: String,
    val logoUrl: String,
    val changePercent: Double?
)