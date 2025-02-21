package com.yaabelozerov.superfinancer.finance.data

import androidx.room.Embedded
import androidx.room.Relation

internal data class TransactionAndGoal(
    @Embedded val transactionEntity: TransactionEntity,
    @Relation(
        parentColumn = "goalId", entityColumn = "id"
    ) val goalEntity: GoalEntity,
)
