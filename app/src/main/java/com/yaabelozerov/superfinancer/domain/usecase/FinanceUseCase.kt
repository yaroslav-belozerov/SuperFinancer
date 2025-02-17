package com.yaabelozerov.superfinancer.domain.usecase

import com.yaabelozerov.superfinancer.Application
import com.yaabelozerov.superfinancer.data.local.room.finance.FinanceDao
import com.yaabelozerov.superfinancer.data.local.room.finance.GoalEntity
import com.yaabelozerov.superfinancer.data.local.room.finance.TransactionEntity
import com.yaabelozerov.superfinancer.domain.model.Goal
import com.yaabelozerov.superfinancer.domain.model.Transaction
import com.yaabelozerov.superfinancer.ui.format
import com.yaabelozerov.superfinancer.ui.toString
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class FinanceUseCase(
    private val financeDao: FinanceDao = Application.financeDao,
) {
    val goalFlow = financeDao.getAllTargetsWithTransactions().map {
        it.map {
            val goal = it.key
            val transactions = it.value
            val rubles = goal.amountInKopecks.div(100.0)
            val transactionRubles = transactions.sumOf { it.valueInKopecks.div(100.0) }
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
                valueInRubles = transaction.valueInKopecks.div(100.0),
                comment = transaction.comment,
                goalName = goal.name,
                timestamp = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(transaction.timestamp), ZoneId.systemDefault()
                ).format()
            )
        }
    }

    suspend fun createGoal(name: String, amountInRubles: Double, image: String) {
        financeDao.createGoal(
            GoalEntity(
                id = 0, name = name, image = image, amountInKopecks = amountInRubles.times(100).toLong()
            )
        )
    }

    suspend fun createTransaction(
        goalId: Long,
        amountInRubles: Double = 0.0,
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
}