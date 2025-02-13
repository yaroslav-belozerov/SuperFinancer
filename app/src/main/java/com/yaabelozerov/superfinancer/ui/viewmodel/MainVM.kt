package com.yaabelozerov.superfinancer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaabelozerov.superfinancer.BuildConfig
import com.yaabelozerov.superfinancer.data.remote.forex.ForexSource
import com.yaabelozerov.superfinancer.data.remote.forex.toDomainOfSymbol
import com.yaabelozerov.superfinancer.domain.model.Forex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ForexState(
    val list: List<Forex> = emptyList(),
    val error: Throwable? = null,
)

data class MainState(
    val forex: ForexState = ForexState(),
)

class MainVM(private val forexSource: ForexSource = ForexSource()) : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    fun fetchForex() {
        viewModelScope.launch(Dispatchers.IO) {
            val resp =
                listOf("GOOG").map {
                    async {
                        forexSource.getRateForSymbol(it, BuildConfig.FINNHUB_TOKEN) to it
                    }
                }.awaitAll()
            if (resp.any { it.first.isFailure }) {
                _state.update { it.copy(forex = it.forex.copy(error = resp.firstOrNull { it.first.isFailure }?.first?.exceptionOrNull())) }
            } else {
                _state.update { it.copy(forex = it.forex.copy(list = resp.mapNotNull { it.first.getOrNull()?.toDomainOfSymbol(it.second) })) }
            }
        }
    }
}