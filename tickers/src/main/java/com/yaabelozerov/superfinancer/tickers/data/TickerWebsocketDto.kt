package com.yaabelozerov.superfinancer.tickers.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TickerStartConnection(
    val type: String = "subscribe",
    val symbol: String
)

@Serializable
internal data class TickerReceiveConnection(
    val data: List<TickerReceiveConnectionElement>,
    val type: String
)

@Serializable
internal data class TickerReceiveConnectionElement(
    @SerialName("p") val price: Double,
    @SerialName("s") val symbol: String,
    @SerialName("t") val timestamp: Long,
    @SerialName("v") val volume: Double,
    @SerialName("c") val conditions: List<String>,
)
