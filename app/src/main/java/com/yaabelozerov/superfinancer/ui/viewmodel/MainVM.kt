package com.yaabelozerov.superfinancer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.yaabelozerov.superfinancer.domain.model.Ticker
import com.yaabelozerov.superfinancer.domain.usecase.StoryPagingConfig
import com.yaabelozerov.superfinancer.domain.usecase.StoryUseCase
import com.yaabelozerov.superfinancer.domain.usecase.TickerUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TickerState(
    val map: Map<String, Ticker> = emptyMap(),
    val error: Throwable? = null,
    val isLoading: Boolean = false,
)

data class MainState(
    val ticker: TickerState = TickerState(),
)

class MainVM(
    private val tickerUseCase: TickerUseCase = TickerUseCase(),
    private val storyUseCase: StoryUseCase = StoryUseCase()
) : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    val storyFlow = Pager(
        PagingConfig(pageSize = StoryUseCase.DefaultConfig.limit)
    ) { storyUseCase }.flow

    fun fetchTickers() {
        viewModelScope.launch(Dispatchers.IO) {
            val lst =
                listOf("GOOG", "AAPL", "MSFT", "TESLA", "AMZN", "META", "WMT", "JPM", "V", "MA")
            _state.update { it.copy(ticker = it.ticker.copy(isLoading = true)) }
            tickerUseCase.tickerFlow(lst).collectLatest { tickerItem ->
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