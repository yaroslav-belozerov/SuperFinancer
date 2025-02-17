package com.yaabelozerov.superfinancer.domain.usecase

import com.yaabelozerov.superfinancer.Application
import com.yaabelozerov.superfinancer.data.local.room.finance.FinanceDao
import com.yaabelozerov.superfinancer.domain.model.Goal
import kotlinx.coroutines.flow.map

class FinanceUseCase(
    private val financeDao: FinanceDao = Application.financeDao
) {
    val goalFlow = financeDao.getAllTargetsWithTransactions().map {
        it.map {
            val goal = it.key
            val transactions = it.value
            val rubles = goal.amountInKopecks.div(100.0)
            Goal(
                goal.id,
                goal.name,
                rubles.toString(),
                transactions.sumOf { it.valueInKopecks.div(100.0) }.div(rubles)
            )
        }
    }
}