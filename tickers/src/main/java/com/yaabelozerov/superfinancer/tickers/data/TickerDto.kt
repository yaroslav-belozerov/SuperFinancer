package com.yaabelozerov.superfinancer.tickers.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TickerDto(
    @SerialName("c") val current: Double,
    @SerialName("d") val delta: Double?,
    @SerialName("dp") val deltaPercent: Double?,
    @SerialName("h") val high: Double,
    @SerialName("l") val low: Double,
    @SerialName("o") val open: Double,
    @SerialName("pc") val previousOpen: Double,
)