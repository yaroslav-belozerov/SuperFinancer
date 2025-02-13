package com.yaabelozerov.superfinancer.domain.model

data class Forex(
    val symbol: String,
    val value: String,
    val changePercent: Double?
)
