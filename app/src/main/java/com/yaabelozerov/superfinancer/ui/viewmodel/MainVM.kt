package com.yaabelozerov.superfinancer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.yaabelozerov.superfinancer.domain.model.Section
import com.yaabelozerov.superfinancer.domain.model.Story
import com.yaabelozerov.superfinancer.domain.model.Ticker
import com.yaabelozerov.superfinancer.domain.usecase.StoriesUseCase
import com.yaabelozerov.superfinancer.domain.usecase.TickerUseCase
import com.yaabelozerov.superfinancer.ui.toString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TickerState(
    val map: Map<String, Ticker> = emptyMap(),
    val error: Throwable? = null,
    val isLoading: Boolean = false,
)

data class SectionState(
    val list: List<Section> = emptyList(),
    val selected: Section? = null,
)

class MainVM(
    private val tickerUseCase: TickerUseCase = TickerUseCase(),
    private val storyUseCase: StoriesUseCase = StoriesUseCase(),
) : ViewModel() {
    private val _tickerState = MutableStateFlow(TickerState())
    val tickerState = _tickerState.asStateFlow()

    private val _sectionState = MutableStateFlow(SectionState())
    val sectionState = _sectionState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    var stories: Flow<PagingData<Story>> = _sectionState.flatMapLatest { currentState ->
        storyUseCase.createFlow(currentState.selected?.key)
    }.cachedIn(viewModelScope)

    init {
        fetchAll()
    }

    fun fetchAll() {
        fetchTickerInfos()
        subscribeToTickers()
        fetchSections()
    }

    private fun fetchTickerInfos() {
        viewModelScope.launch(Dispatchers.IO) {
            _tickerState.update {
                it.copy(
                    map = tickerUseCase.getFullInfoForSymbols(tickerList)
                )
            }
        }
    }

    private fun subscribeToTickers() {
        viewModelScope.launch(Dispatchers.IO) {
            tickerUseCase.tickerConnectionFlow(tickerList).collectLatest { newValue ->
                _tickerState.update {
                    val inMap = it.map[newValue.first]
                    if (inMap != null) {
                        it.copy(
                            map = it.map.plus(
                                newValue.first to inMap.copy(
                                    value = newValue.second.toString(
                                        2
                                    )
                                )
                            )
                        )
                    } else it
                }
            }
        }
    }

    private fun fetchSections() {
        viewModelScope.launch(Dispatchers.IO) {
            val sections = storyUseCase.getSections()
            _sectionState.update {
                it.copy(list = sections,
                    selected = sections.firstOrNull { _sectionState.value.selected?.key == it.key })
            }
        }
    }

    fun setSection(section: Section?) {
        if (section?.key == _sectionState.value.selected?.key) {
            _sectionState.update { it.copy(selected = null) }
        } else {
            _sectionState.update { it.copy(selected = section) }
        }
    }

    companion object {
        private val tickerList =
            listOf("GOOG", "AAPL", "MSFT", "TESLA", "AMZN", "META", "WMT", "JPM", "V", "MA")
    }
}