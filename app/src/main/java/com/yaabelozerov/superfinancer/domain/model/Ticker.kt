package com.yaabelozerov.superfinancer.domain.model

data class Ticker(
    val value: String,
    val name: String,
    val currency: String,
    val logoUrl: String,
    val changePercent: Double?
)
