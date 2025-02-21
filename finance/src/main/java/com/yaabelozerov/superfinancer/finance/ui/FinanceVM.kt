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
    data class CreateGoal(val name: String, val amountInRubles: Long, val image: String, val date: Long?) :
        FinanceScreenEvent

    data class MakeTransaction(
        val targetGoalId: Long,
        val amountInRubles: Long,
        val comment: String,
        val isWithdrawal: Boolean
    ) : FinanceScreenEvent

    data class DeleteGoal(val goal: Goal) : FinanceScreenEvent
    data class DeleteTransaction(val id: Long, val goalId: Long) : FinanceScreenEvent
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
                    event.name, event.amountInRubles, event.image, event.date
                )

                is FinanceScreenEvent.DeleteGoal -> financeUseCase.closeGoal(event.goal)
                is FinanceScreenEvent.MakeTransaction -> financeUseCase.createTransaction(
                    goalId = event.targetGoalId,
                    amountInRubles = event.amountInRubles,
                    comment = event.comment,
                    isWithdrawal = event.isWithdrawal
                )

                is FinanceScreenEvent.DeleteTransaction -> financeUseCase.deleteTransaction(event.id, event.goalId)
            }
        }
    }
}