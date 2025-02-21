package com.yaabelozerov.superfinancer.finance.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
internal interface FinanceDao {
    @Query("SELECT * FROM goals LEFT OUTER JOIN transactions ON goalId = goals.id WHERE enabled = 1")
    fun getAllTargetsWithTransactions(): Flow<Map<GoalEntity, List<TransactionEntity>>>

    @Transaction
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionAndGoal>>

    @Query("SELECT SUM(amountInKopecks) FROM goals WHERE enabled = 1")
    fun totalGoalInKopecks(): Flow<Long>

    @Query("SELECT SUM(valueInKopecks) from transactions WHERE isWithdrawal = 0")
    fun totalTransactionAmountInKopecks(): Flow<Long>

    @Query("SELECT SUM(valueInKopecks) FROM transactions WHERE goalId = :id AND isWithdrawal = 0")
    suspend fun totalTransactionAmountInKopecksByGoalId(id: Long): Long

    @Query("DELETE FROM transactions WHERE goalId = :id")
    suspend fun deleteAllTransactionsByGoalId(id: Long)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteGoalWithId(id: Long)

    @Query("UPDATE goals SET enabled = 0 WHERE id = :id")
    suspend fun deactivateGoalWithId(id: Long)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)

    @Insert
    suspend fun createGoal(goalEntity: GoalEntity): Long

    @Upsert
    suspend fun upsertTransaction(transactionEntity: TransactionEntity): Long
}