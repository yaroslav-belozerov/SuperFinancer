package com.yaabelozerov.superfinancer.data.local.room.finance

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {
    @Query("SELECT * FROM goals JOIN transactions ON goalId = goals.id")
    fun getAllTargetsWithTransactions(): Flow<Map<GoalEntity, List<TransactionEntity>>>
}