package com.yaabelozerov.superfinancer.domain.model

data class Transaction(
    val id: Long,
    val valueInRubles: Long,
    val comment: String,
    val goalName: String,
    val timestamp: String
)