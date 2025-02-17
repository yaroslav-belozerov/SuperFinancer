package com.yaabelozerov.superfinancer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.superfinancer.domain.model.Goal
import com.yaabelozerov.superfinancer.domain.model.Transaction
import com.yaabelozerov.superfinancer.domain.usecase.FinanceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FinanceState(
    val goals: List<Goal> = emptyList(),
    val transactions: List<Transaction> = emptyList()
)

class FinanceVM(
    private val financeUseCase: FinanceUseCase = FinanceUseCase()
): ViewModel() {
    private val _state = MutableStateFlow(FinanceState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            financeUseCase.goalFlow.collect { goals ->
                _state.update { it.copy(goals = goals) }
            }
        }
        viewModelScope.launch {
            financeUseCase.transactionFlow.collect { transactions ->
                _state.update { it.copy(transactions = transactions) }
            }
        }
    }

    fun createGoal(name: String, amountInRubles: Double) {
        viewModelScope.launch {
            financeUseCase.createGoal(name = name, amountInRubles = amountInRubles)
        }
    }

    fun makeTransaction(goalId: Long, amountInRubles: Double, comment: String) {
        viewModelScope.launch {
            financeUseCase.createTransaction(
                goalId = goalId,
                amountInRubles = amountInRubles,
                comment = comment
            )
        }
    }
}