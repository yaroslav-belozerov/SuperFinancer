package com.yaabelozerov.superfinancer.ui.viewmodel

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.yaabelozerov.superfinancer.common.CommonModule
import com.yaabelozerov.superfinancer.common.local.config.DataStoreManager
import com.yaabelozerov.superfinancer.common.util.format
import com.yaabelozerov.superfinancer.common.util.toString
import com.yaabelozerov.superfinancer.stories.domain.Section
import com.yaabelozerov.superfinancer.stories.domain.Story
import com.yaabelozerov.superfinancer.stories.domain.StoriesUseCase
import com.yaabelozerov.superfinancer.stories.ui.SectionUiState
import com.yaabelozerov.superfinancer.tickers.domain.TickerUseCase
import com.yaabelozerov.superfinancer.tickers.ui.TickerUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainVM(
    private val tickerUseCase: TickerUseCase = TickerUseCase(),
    private val storyUseCase: StoriesUseCase = StoriesUseCase(),
) : ViewModel() {
    private val _tickerState = MutableStateFlow(TickerUiState())
    val tickerState = _tickerState.asStateFlow()

    private val _sectionUiState = MutableStateFlow(SectionUiState())
    val sectionState = _sectionUiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    var stories: Flow<PagingData<Story>> = _sectionUiState.flatMapLatest { currentState ->
        storyUseCase.createFlow(currentState.selected?.key)
    }.cachedIn(viewModelScope)

    init {
        fetchTickerInfos()
        fetchSections()
        subscribeToTickers()
    }

    fun refreshAll() {
        fetchTickerInfos()
        fetchSections(true)
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
                    isLoading = false, lastUpdated = LocalDateTime.now().format()
                )
            }
        }
    }

    private fun subscribeToTickers() {
        viewModelScope.launch(Dispatchers.IO) {
            tickerUseCase.startConnectionsForTickers(TickerUseCase.defaultTickers)
            tickerUseCase.tickerConnectionFlow.collectLatest { newValue ->
                _tickerState.update {
                    val inMap = it.map[newValue.first]
                    if (inMap != null) {
                        it.copy(
                            map = it.map.plus(
                                newValue.first to inMap.copy(
                                    value = newValue.second.toString(2)
                                )
                            ), lastUpdated = LocalDateTime.now().format()
                        )
                    } else it
                }
            }
        }
    }

    private fun fetchSections(forceRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            val savedSections = StoriesUseCase.getSavedSections().first()
            val sections = if (forceRefresh || savedSections == null) {
                println("refreshing sections")
                storyUseCase.getSections { StoriesUseCase.setSavedSections(it) }
            } else savedSections.also { println("using saved sections") }
            sections?.let {
                _sectionUiState.update { st ->
                    st.copy(list = it,
                        selected = it.firstOrNull { _sectionUiState.value.selected?.key == it.key })
                }
            }
        }
    }

    fun setSection(section: Section?) {
        if (section?.key == _sectionUiState.value.selected?.key) {
            _sectionUiState.update { it.copy(selected = null) }
        } else {
            _sectionUiState.update { it.copy(selected = section) }
        }
    }

    fun setSection(sectionName: String) {
        _sectionUiState.value.list.firstOrNull { it.name == sectionName }?.let { setSection(it) }
    }
}