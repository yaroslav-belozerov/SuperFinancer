package com.yaabelozerov.superfinancer.data.remote.finnhub.ticker

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val country: String,
    val currency: String,
    val exchange: String,
    val ipo: String,
    val marketCapitalization: Double,
    val name: String,
    val phone: String,
    val shareOutstanding: Double,
    val ticker: String,
    @SerialName("weburl") val webUrl: String,
    val logo: String,
    val finnhubIndustry: String,
)
