package com.yaabelozerov.superfinancer.tickers.ui

import com.yaabelozerov.superfinancer.tickers.data.RecommendationDto
import com.yaabelozerov.superfinancer.tickers.domain.Earning
import com.yaabelozerov.superfinancer.tickers.domain.Recommendation
import com.yaabelozerov.superfinancer.tickers.domain.Ticker

internal data class TickerUiState(
    val map: Map<String, Ticker> = emptyMap(),
    val error: Throwable? = null,
    val isLoading: Boolean = false,
    val lastUpdated: String = "",
)

internal data class TickerDetailUiState(
    val recommendations: List<Recommendation> = emptyList(),
    val earnings: List<Earning> = emptyList()
)