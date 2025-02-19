package com.yaabelozerov.superfinancer.finance.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val valueInKopecks: Long,
    val timestamp: Long,
    val comment: String,
    val goalId: Long
)
