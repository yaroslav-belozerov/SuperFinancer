package com.yaabelozerov.superfinancer.ui.viewmodel

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.superfinancer.finance.domain.SearchItem
import com.yaabelozerov.superfinancer.domain.usecase.SearchUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchState(
    val query: String = "",
    val searchResults: SnapshotStateList<SearchItem> = SnapshotStateList(),
)

class SearchVM(
    private val searchUseCase: SearchUseCase = SearchUseCase(),
) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    private var currentJob: Job? = null

    fun onQueryChange(query: String) {
        _state.update { SearchState(query = query) }
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            delay(500L)
            println("job launched")
            if (query.length >= 3) {
                val list =
                    SnapshotStateList<SearchItem>()
                searchUseCase.search(query).flowOn(Dispatchers.IO).collectLatest {
                    list.add(it)
                    _state.update {
                        it.copy(
                            searchResults = list
                        )
                    }
                }
            }
        }
    }
}