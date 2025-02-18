package com.yaabelozerov.superfinancer.domain.usecase

import com.yaabelozerov.superfinancer.Application
import com.yaabelozerov.superfinancer.data.local.media.MediaManager
import com.yaabelozerov.superfinancer.data.local.room.finance.FinanceDao
import com.yaabelozerov.superfinancer.data.local.room.finance.GoalEntity
import com.yaabelozerov.superfinancer.data.local.room.finance.TransactionEntity
import com.yaabelozerov.superfinancer.domain.model.Goal
import com.yaabelozerov.superfinancer.domain.model.Transaction
import com.yaabelozerov.superfinancer.ui.format
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class FinanceUseCase(
    private val financeDao: FinanceDao = Application.financeDao,
    private val mediaManager: MediaManager = Application.mediaManager
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
        Application.financeTransaction {
            financeDao.deleteAllTransactionsByGoalId(goal.id)
            financeDao.deleteGoalWithId(goal.id)
            mediaManager.removeMedia(goal.image)
        }
    }
}