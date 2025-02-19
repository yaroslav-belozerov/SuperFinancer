package com.yaabelozerov.superfinancer.tickers.ui

import com.yaabelozerov.superfinancer.tickers.domain.Ticker

data class TickerUiState(
    val map: Map<String, Ticker> = emptyMap(),
    val error: Throwable? = null,
    val isLoading: Boolean = false,
    val lastUpdated: String = "",
)
