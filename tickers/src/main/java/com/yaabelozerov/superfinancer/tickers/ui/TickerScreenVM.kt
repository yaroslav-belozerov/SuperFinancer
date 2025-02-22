package com.yaabelozerov.superfinancer.tickers.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.superfinancer.tickers.domain.TickerDetailUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


internal class TickerScreenVM(
    private val useCase: TickerDetailUseCase = TickerDetailUseCase()
): ViewModel() {
    private val _state = MutableStateFlow(TickerDetailUiState())
    val state = _state.asStateFlow()

    fun fetchInfoForSymbol(symbol: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(recommendations = useCase.getRecommendations(symbol))
            }
        }
        viewModelScope.launch {
            _state.update {
                it.copy(earnings = useCase.getEarnings(symbol))
            }
        }
    }
}