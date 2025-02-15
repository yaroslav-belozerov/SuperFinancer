package com.yaabelozerov.superfinancer.data.local.configuration

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Config(
    @SerialName("tickers") val defaultTickers: List<String>,
)
