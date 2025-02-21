package com.yaabelozerov.superfinancer.finance.domain

internal data class Goal(
    val id: Long,
    val image: String,
    val name: String,
    val currentRubles: Long,
    val maxRubles: Long,
)
