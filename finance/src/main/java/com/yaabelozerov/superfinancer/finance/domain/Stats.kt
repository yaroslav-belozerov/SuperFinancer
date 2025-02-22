package com.yaabelozerov.superfinancer.finance.domain

import java.time.LocalDateTime

data class Stats(
    val firstTransactionDate: String? = null,
    val lastTransactionDate: String? = null,
    val openGoals: Pair<Int, Long> = 0 to 0,
    val closedGoals: Pair<Int, Long> = 0 to 0
)