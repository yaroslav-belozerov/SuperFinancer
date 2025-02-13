package com.yaabelozerov.superfinancer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.yaabelozerov.superfinancer.data.remote.nytimes.stories.SectionDto
import com.yaabelozerov.superfinancer.domain.model.Section
import com.yaabelozerov.superfinancer.domain.model.Story
import com.yaabelozerov.superfinancer.domain.model.Ticker
import com.yaabelozerov.superfinancer.domain.usecase.StoriesUseCase
import com.yaabelozerov.superfinancer.domain.usecase.TickerUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.all
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TickerState(
    val map: Map<String, Ticker> = emptyMap(),
    val error: Throwable? = null,
    val isLoading: Boolean = false,
)

data class SectionState(
    val list: List<Section> = emptyList(),
    val selected: Section? = null
)

data class MainState(
    val ticker: TickerState = TickerState(),
    val sections: SectionState = SectionState()
)

class MainVM(
    private val tickerUseCase: TickerUseCase = TickerUseCase(),
    private val storyUseCase: StoriesUseCase = StoriesUseCase()
) : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    var stories: Flow<PagingData<Story>> = updateStoryFlow(null)
        private set

    fun fetchAll() {
        fetchTickers()
        fetchSections()
    }

    private fun fetchTickers() {
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

    private fun fetchSections() {
        viewModelScope.launch {
            _state.update { it.copy(sections = it.sections.copy(list = storyUseCase.getSections(), selected = null)) }
        }
    }

    fun setSection(section: Section?) {
        if (section?.key == _state.value.sections.selected?.key) return
        _state.update { it.copy(sections = it.sections.copy(selected = section)) }
        updateStoryFlow(section?.key)
    }

    private fun updateStoryFlow(key: String?): Flow<PagingData<Story>> {
        return storyUseCase.createStoryFlow(key.also { println("Key $it") }).cachedIn(viewModelScope)
    }
}