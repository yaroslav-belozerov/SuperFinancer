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

@Serializable
data class TickerStartConnection(
    val type: String = "subscribe",
    val symbol: String
)

@Serializable
data class TickerReceiveConnection(
    val data: List<TickerReceiveConnectionElement>,
    val type: String
)

@Serializable
data class TickerReceiveConnectionElement(
    @SerialName("p") val price: Double,
    @SerialName("s") val symbol: String,
    @SerialName("t") val timestamp: Long,
    @SerialName("v") val volume: Double,
    @SerialName("c") val conditions: List<String>,
)