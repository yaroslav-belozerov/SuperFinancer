package com.yaabelozerov.superfinancer.finance.domain

internal data class Transaction(
    val id: Long,
    val valueInRubles: Long,
    val comment: String,
    val goalName: String,
    val timestamp: String
)