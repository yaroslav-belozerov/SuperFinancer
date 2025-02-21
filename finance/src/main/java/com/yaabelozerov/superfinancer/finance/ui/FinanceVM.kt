package com.yaabelozerov.superfinancer.finance.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.superfinancer.finance.domain.Goal
import com.yaabelozerov.superfinancer.finance.domain.Transaction
import com.yaabelozerov.superfinancer.finance.domain.FinanceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal data class FinanceState(
    val goals: List<Goal> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val totalGoal: Long = 1,
    val totalAmount: Long = 0,
)

internal sealed interface FinanceScreenEvent {
    data class CreateGoal(val name: String, val amountInRubles: Long, val image: String) :
        FinanceScreenEvent

    data class MakeTransaction(
        val targetGoalId: Long,
        val amountInRubles: Long,
        val comment: String,
    ) : FinanceScreenEvent

    data class DeleteGoal(val goal: Goal) : FinanceScreenEvent
    data class DeleteTransaction(val id: Long) : FinanceScreenEvent
}

internal class FinanceVM(
    private val financeUseCase: FinanceUseCase = FinanceUseCase(),
) : ViewModel() {
    private val _state = MutableStateFlow(FinanceState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            financeUseCase.goalFlow.collect { _state.update { st -> st.copy(goals = it) } }
        }
        viewModelScope.launch {
            financeUseCase.transactionFlow.collect { _state.update { st -> st.copy(transactions = it) } }
        }
        viewModelScope.launch {
            financeUseCase.totalGoalFlow.collect { _state.update { st -> st.copy(totalGoal = it) } }
        }
        viewModelScope.launch {
            financeUseCase.totalAmountFlow.collect { _state.update { st -> st.copy(totalAmount = it) } }
        }
    }

    fun onEvent(event: FinanceScreenEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            when (event) {
                is FinanceScreenEvent.CreateGoal -> financeUseCase.createGoal(
                    event.name, event.amountInRubles, event.image
                )

                is FinanceScreenEvent.DeleteGoal -> financeUseCase.deleteGoal(event.goal)
                is FinanceScreenEvent.MakeTransaction -> financeUseCase.createTransaction(
                    goalId = event.targetGoalId,
                    amountInRubles = event.amountInRubles,
                    comment = event.comment
                )

                is FinanceScreenEvent.DeleteTransaction -> financeUseCase.deleteTransaction(event.id)
            }
        }
    }
}