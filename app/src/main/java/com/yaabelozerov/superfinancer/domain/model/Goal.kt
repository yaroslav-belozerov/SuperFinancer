package com.yaabelozerov.superfinancer.domain.model

data class Goal(
    val id: Long,
    val name: String,
    val maxRubles: String,
    val completion: Double
)
