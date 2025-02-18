package com.yaabelozerov.superfinancer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.superfinancer.domain.model.Goal
import com.yaabelozerov.superfinancer.domain.model.Transaction
import com.yaabelozerov.superfinancer.domain.usecase.FinanceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FinanceState(
    val goals: List<Goal> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val totalGoal: Double = 1.0,
    val totalAmount: Double = 0.0,
)

sealed interface FinanceScreenEvent {
    data class CreateGoal(val name: String, val amountInRubles: Double, val image: String) :
        FinanceScreenEvent

    data class MakeTransaction(
        val targetGoalId: Long,
        val amountInRubles: Double,
        val comment: String,
    ) : FinanceScreenEvent

    data class DeleteGoal(val goal: Goal) : FinanceScreenEvent
}

class FinanceVM(
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
            }
        }
    }
}