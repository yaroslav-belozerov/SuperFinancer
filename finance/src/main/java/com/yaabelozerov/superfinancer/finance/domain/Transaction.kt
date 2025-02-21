package com.yaabelozerov.superfinancer.finance.domain

internal data class Transaction(
    val id: Long,
    val valueInRubles: Long,
    val comment: String,
    val goal: Pair<Long, String>,
    val timestamp: String
)