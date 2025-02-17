package com.yaabelozerov.superfinancer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.superfinancer.domain.model.Goal
import com.yaabelozerov.superfinancer.domain.usecase.FinanceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FinanceState(
    val goals: List<Goal> = emptyList()
)

class FinanceVM(
    private val financeUseCase: FinanceUseCase = FinanceUseCase()
): ViewModel() {
    private val _state = MutableStateFlow(FinanceState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            financeUseCase.goalFlow.collect { targets ->
                _state.update { it.copy(goals = targets) }
            }
        }
    }
}