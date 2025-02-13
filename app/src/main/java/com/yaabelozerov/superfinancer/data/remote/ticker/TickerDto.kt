package com.yaabelozerov.superfinancer.data.remote.ticker

import com.yaabelozerov.superfinancer.domain.model.Ticker
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

fun TickerDto.isValid(): Boolean {
    return current != 0.0 && high != 0.0 && low != 0.0 && open != 0.0 && previousOpen != 0.0 && deltaPercent != null && delta != null
}