package com.yaabelozerov.superfinancer.finance.domain

import androidx.room.withTransaction
import com.yaabelozerov.superfinancer.common.CommonModule
import com.yaabelozerov.superfinancer.common.local.MediaManager
import com.yaabelozerov.superfinancer.common.util.format
import com.yaabelozerov.superfinancer.finance.FinanceModule
import com.yaabelozerov.superfinancer.finance.data.FinanceDao
import com.yaabelozerov.superfinancer.finance.data.GoalEntity
import com.yaabelozerov.superfinancer.finance.data.StatsDao
import com.yaabelozerov.superfinancer.finance.data.TransactionEntity
import com.yaabelozerov.superfinancer.finance.data.toRublePair
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

internal class FinanceUseCase(
    private val financeDao: FinanceDao = FinanceModule.financeDao,
    private val financeStatsDao: StatsDao = FinanceModule.statsDao,
    private val mediaManager: MediaManager = CommonModule.mediaManager,
) {
    val statsFlow = combine(
        financeStatsDao.getFirstTransactionTimestamp(),
        financeStatsDao.getLastTransactionTimestamp(),
        financeStatsDao.openGoalsCountAndSum(),
        financeStatsDao.closedGoalsCountAndSum()
    ) { first, last, open, closed ->
        Stats(firstTransactionDate = first?.let {
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(it), ZoneId.systemDefault()
            ).format()
        }, lastTransactionDate = last?.let {
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(it), ZoneId.systemDefault()
            ).format()
        }, openGoals = open.toRublePair(), closedGoals = closed.toRublePair()
        )
    }

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
                expiresAt = it.key.expiresAt?.let {
                    if (it < System.currentTimeMillis()) "Deadline missed" else LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(it), ZoneId.systemDefault()
                    ).format(withTime = false)
                },
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
                goal = goal.id to goal.name,
                timestamp = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(transaction.timestamp), ZoneId.systemDefault()
                ).format(),
                isWithdrawal = transaction.isWithdrawal
            )
        }
    }

    val totalGoalFlow = financeDao.totalGoalInKopecks().map { it.div(100L) }
    val totalAmountFlow = financeDao.totalTransactionAmountInKopecks().map { it.div(100L) }

    suspend fun createGoal(name: String, amountInRubles: Long, image: String, expiry: Long?) {
        financeDao.createGoal(
            GoalEntity(
                id = 0,
                name = name,
                image = image,
                amountInKopecks = amountInRubles.times(100),
                expiresAt = expiry,
                enabled = true
            )
        )
    }

    suspend fun createTransaction(
        goalId: Long,
        amountInRubles: Long,
        comment: String = "",
        isWithdrawal: Boolean,
    ) {
        financeDao.upsertTransaction(
            TransactionEntity(
                id = 0,
                valueInKopecks = amountInRubles.times(100).toLong(),
                timestamp = System.currentTimeMillis(),
                comment = comment,
                goalId = goalId,
                isWithdrawal = isWithdrawal
            )
        )
    }

    suspend fun closeGoal(goal: Goal) {
        FinanceModule.financeDb.withTransaction {
            val total = financeDao.totalTransactionAmountInKopecksByGoalId(goal.id)
            financeDao.deleteAllTransactionsByGoalId(goal.id)
            if (total > 0) {
                financeDao.upsertTransaction(
                    TransactionEntity(
                        id = 0,
                        valueInKopecks = total,
                        timestamp = System.currentTimeMillis(),
                        comment = "Goal \"${goal.name}}\" closed",
                        goalId = goal.id,
                        isWithdrawal = true
                    )
                )
                financeDao.deactivateGoalWithId(goal.id)
            } else {
                financeDao.deleteGoalWithId(goal.id)
            }
            mediaManager.removeMedia(goal.image)
        }
    }

    suspend fun deleteTransaction(id: Long) {
        financeDao.deleteTransaction(id)
    }
}