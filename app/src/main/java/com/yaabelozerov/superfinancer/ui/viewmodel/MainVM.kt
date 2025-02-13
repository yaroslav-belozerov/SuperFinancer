package com.yaabelozerov.superfinancer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.superfinancer.data.remote.finnhub.FinnhubSource
import com.yaabelozerov.superfinancer.domain.model.Ticker
import com.yaabelozerov.superfinancer.domain.usecase.TickerUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TickerState(
    val map: Map<String, Ticker> = emptyMap(),
    val error: Throwable? = null,
    val isLoading: Boolean = false
)

data class MainState(
    val ticker: TickerState = TickerState()
)

class MainVM(private val useCase: TickerUseCase = TickerUseCase()) : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    fun fetchTickers() {
        viewModelScope.launch(Dispatchers.IO) {
            val lst = listOf("GOOG", "AAPL", "MSFT", "TESLA", "AMZN", "META", "WMT", "JPM", "V", "MA")
            _state.update { it.copy(ticker = it.ticker.copy(isLoading = true)) }
            useCase.tickerFlow(lst).collectLatest { tickerItem ->
                _state.update {
                    it.copy(
                        ticker = if (tickerItem.second != null) {
                            it.ticker.copy(map = it.ticker.map.plus(tickerItem.first to tickerItem.second!!))
                        } else {
                            it.ticker.copy(error = tickerItem.third)
                        }
                    )
                }
            }
            _state.update { it.copy(ticker = it.ticker.copy(isLoading = false)) }
        }
    }
}