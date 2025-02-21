package com.yaabelozerov.superfinancer.finance.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
internal interface FinanceDao {
    @Query("SELECT * FROM goals LEFT OUTER JOIN transactions ON goalId = goals.id")
    fun getAllTargetsWithTransactions(): Flow<Map<GoalEntity, List<TransactionEntity>>>

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionAndGoal>>

    @Query("SELECT SUM(amountInKopecks) FROM goals")
    fun totalGoalInKopecks(): Flow<Long>

    @Query("SELECT SUM(valueInKopecks) from transactions")
    fun totalTransactionAmountInKopecks(): Flow<Long>

    @Query("DELETE FROM transactions WHERE goalId = :id")
    suspend fun deleteAllTransactionsByGoalId(id: Long)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteGoalWithId(id: Long)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)

    @Insert
    suspend fun createGoal(goalEntity: GoalEntity): Long

    @Upsert
    suspend fun upsertTransaction(transactionEntity: TransactionEntity): Long
}