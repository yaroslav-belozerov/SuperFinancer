package com.yaabelozerov.superfinancer.common.local.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Config(
    @SerialName("tickers") val defaultTickers: List<String>,
)
