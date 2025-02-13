package com.yaabelozerov.superfinancer.data.remote.forex

import com.yaabelozerov.superfinancer.domain.model.Forex
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForexDto(
    @SerialName("c") val current: Double,
    @SerialName("d") val delta: Double?,
    @SerialName("dp") val deltaPercent: Double?,
    @SerialName("h") val high: Double,
    @SerialName("l") val low: Double,
    @SerialName("o") val open: Double,
    @SerialName("pc") val previousOpen: Double,
)

fun ForexDto.isValid(): Boolean {
    return current != 0.0 && high != 0.0 && low != 0.0 && open != 0.0 && previousOpen != 0.0 && deltaPercent != null && delta != null
}

fun ForexDto.toDomainOfSymbol(symbol: String) = Forex(
    symbol = symbol,
    value = current.toString(),
    changePercent = deltaPercent,
)