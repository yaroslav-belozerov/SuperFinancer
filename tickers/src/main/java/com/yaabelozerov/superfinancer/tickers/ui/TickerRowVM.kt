package com.yaabelozerov.superfinancer.tickers.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.superfinancer.common.util.format
import com.yaabelozerov.superfinancer.common.util.toString
import com.yaabelozerov.superfinancer.tickers.domain.TickerUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

internal class TickerRowVM(
    private val tickerUseCase: TickerUseCase = TickerUseCase(),
): ViewModel() {
    private val _tickerState = MutableStateFlow(TickerUiState())
    val state = _tickerState.asStateFlow()

    init {
        fetchTickerInfos()
        subscribeToTickers()
    }

    fun refresh() {
        fetchTickerInfos()
        subscribeToTickers()
    }

    private fun fetchTickerInfos() {
        viewModelScope.launch(Dispatchers.IO) {
            _tickerState.update { it.copy(isLoading = true) }
            _tickerState.update {
                it.copy(
                    map = tickerUseCase.getFullInfoForSymbols(TickerUseCase.defaultTickers)
                )
            }
            _tickerState.update {
                it.copy(
                    isLoading = false, lastUpdated = LocalDateTime.now().format(tickerUseCase.context)
                )
            }
        }
    }

    private fun subscribeToTickers() {
        viewModelScope.launch {
            launch {
                tickerUseCase.startConnectionsForTickers(TickerUseCase.defaultTickers)
            }
            tickerUseCase.tickerConnectionFlow.collect { newValue ->
                _tickerState.update {
                    val inMap = it.map[newValue.first]
                    if (inMap != null) {
                        it.copy(
                            map = it.map.plus(
                                newValue.first to inMap.copy(
                                    value = newValue.second.toString(2)
                                )
                            ), lastUpdated = LocalDateTime.now().format(tickerUseCase.context)
                        )
                    } else it
                }
            }
        }
    }
}