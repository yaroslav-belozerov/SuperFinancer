package com.yaabelozerov.superfinancer.domain.usecase

import com.yaabelozerov.superfinancer.data.remote.finnhub.FinnhubSource
import com.yaabelozerov.superfinancer.data.remote.nytimes.NytSource
import com.yaabelozerov.superfinancer.domain.model.SearchItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class SearchUseCase(
    private val storiesSource: NytSource = NytSource(),
    private val finnhubSource: FinnhubSource = FinnhubSource(),
) {
    fun search(query: String): Flow<SearchItem> = flow {
        storiesSource.searchStories(query).also {
            println(it.exceptionOrNull())
        }.getOrNull()?.response?.docs?.forEach {
            emit(
                SearchItem(
                    type = "Story",
                    title = it.headline.main,
                    description = it.byline.original,
                    iconUrl = it.multimedia.firstOrNull { it.subtype == "thumbnail" }?.url?.let { "https://www.nytimes.com/$it" }
                )
            )
        }
        finnhubSource.searchSymbol(query).also {
            println(it.exceptionOrNull())
        }.getOrNull()?.result?.forEach {
            val info = finnhubSource.getInfoForSymbol(it.symbol).getOrNull()
            info?.let {
                emit(
                    SearchItem(
                        type = "Ticker", title = it.name, description = it.ticker, iconUrl = it.logo
                    )
                )
            }
        }
    }
}