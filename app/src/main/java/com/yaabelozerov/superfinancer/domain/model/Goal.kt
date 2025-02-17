package com.yaabelozerov.superfinancer.domain.model

data class Goal(
    val id: Long,
    val image: String,
    val name: String,
    val currentRubles: Double,
    val maxRubles: Double,
)
