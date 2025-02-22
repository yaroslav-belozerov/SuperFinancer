package com.yaabelozerov.superfinancer.tickers.domain

data class Recommendation(
    val date: String,
    val buy: Int,
    val hold: Int,
    val sell: Int,
    val strongBuy: Int,
    val strongSell: Int
)
