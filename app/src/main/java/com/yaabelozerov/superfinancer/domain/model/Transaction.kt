package com.yaabelozerov.superfinancer.domain.model

data class Transaction(
    val id: Long,
    val valueInRubles: Double,
    val comment: String,
    val goalName: String,
    val timestamp: String
)