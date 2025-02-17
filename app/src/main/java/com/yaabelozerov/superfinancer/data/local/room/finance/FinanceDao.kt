package com.yaabelozerov.superfinancer.data.local.room.finance

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {
    @Query("SELECT * FROM goals LEFT OUTER JOIN transactions ON goalId = goals.id")
    fun getAllTargetsWithTransactions(): Flow<Map<GoalEntity, List<TransactionEntity>>>

    @Insert
    suspend fun createGoal(goalEntity: GoalEntity): Long

    @Upsert
    suspend fun upsertTransaction(transactionEntity: TransactionEntity): Long
}