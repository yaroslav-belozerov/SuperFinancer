package com.yaabelozerov.superfinancer.finance.domain

import androidx.room.withTransaction
import com.yaabelozerov.superfinancer.common.CommonModule
import com.yaabelozerov.superfinancer.common.local.MediaManager
import com.yaabelozerov.superfinancer.common.util.format
import com.yaabelozerov.superfinancer.finance.FinanceModule
import com.yaabelozerov.superfinancer.finance.data.FinanceDao
import com.yaabelozerov.superfinancer.finance.data.GoalEntity
import com.yaabelozerov.superfinancer.finance.data.TransactionEntity
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

internal class FinanceUseCase(
    private val financeDao: FinanceDao = FinanceModule.financeDao,
    private val mediaManager: MediaManager = CommonModule.mediaManager
) {
    val goalFlow = financeDao.getAllTargetsWithTransactions().map {
        it.map {
            val goal = it.key
            val transactions = it.value
            val rubles = goal.amountInKopecks.div(100L)
            val transactionRubles = transactions.sumOf { it.valueInKopecks.div(100L) }
            Goal(
                id = goal.id,
                image = goal.image,
                name = goal.name,
                currentRubles = transactionRubles,
                maxRubles = rubles,
            )
        }
    }

    val transactionFlow = financeDao.getAllTransactions().map {
        it.map {
            val (transaction, goal) = it
            Transaction(
                id = transaction.id,
                valueInRubles = transaction.valueInKopecks.div(100L),
                comment = transaction.comment,
                goalName = goal.name,
                timestamp = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(transaction.timestamp), ZoneId.systemDefault()
                ).format()
            )
        }
    }

    val totalGoalFlow = financeDao.totalGoalInKopecks().map { it.div(100L) }
    val totalAmountFlow = financeDao.totalTransactionAmountInKopecks().map { it.div(100L) }

    suspend fun createGoal(name: String, amountInRubles: Long, image: String) {
        financeDao.createGoal(
            GoalEntity(
                id = 0, name = name, image = image, amountInKopecks = amountInRubles.times(100)
            )
        )
    }

    suspend fun createTransaction(
        goalId: Long,
        amountInRubles: Long,
        comment: String = "",
    ) {
        financeDao.upsertTransaction(
            TransactionEntity(
                id = 0,
                valueInKopecks = amountInRubles.times(100).toLong(),
                timestamp = System.currentTimeMillis(),
                comment = comment,
                goalId = goalId
            )
        )
    }

    suspend fun deleteGoal(goal: Goal) {
        FinanceModule.financeDb.withTransaction {
            financeDao.deleteAllTransactionsByGoalId(goal.id)
            financeDao.deleteGoalWithId(goal.id)
            mediaManager.removeMedia(goal.image)
        }
    }
}