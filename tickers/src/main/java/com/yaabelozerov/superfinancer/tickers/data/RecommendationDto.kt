package com.yaabelozerov.superfinancer.tickers.data

import kotlinx.serialization.Serializable

@Serializable
data class RecommendationDto(
    val buy: Int,
    val hold: Int,
    val period: String,
    val sell: Int,
    val strongBuy: Int,
    val strongSell: Int,
    val symbol: String,
)
