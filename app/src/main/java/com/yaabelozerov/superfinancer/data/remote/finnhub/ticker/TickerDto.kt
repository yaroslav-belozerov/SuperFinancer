package com.yaabelozerov.superfinancer.data.remote.finnhub.ticker

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TickerDto(
    @SerialName("c") val current: Double,
    @SerialName("d") val delta: Double?,
    @SerialName("dp") val deltaPercent: Double?,
    @SerialName("h") val high: Double,
    @SerialName("l") val low: Double,
    @SerialName("o") val open: Double,
    @SerialName("pc") val previousOpen: Double,
)