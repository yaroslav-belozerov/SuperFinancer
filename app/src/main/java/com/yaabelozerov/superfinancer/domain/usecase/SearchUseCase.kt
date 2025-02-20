package com.yaabelozerov.superfinancer.domain.usecase

import com.yaabelozerov.superfinancer.stories.data.remote.NytSource
import com.yaabelozerov.superfinancer.finance.domain.SearchItem
import com.yaabelozerov.superfinancer.finance.domain.SearchItemType
import com.yaabelozerov.superfinancer.tickers.data.FinnhubSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchUseCase(
    private val storiesSource: NytSource = NytSource(),
    private val finnhubSource: FinnhubSource = FinnhubSource(),
) {
    fun search(query: String): Flow<SearchItem> = flow {
        storiesSource.searchStories(query).also {
            println(it.exceptionOrNull())
        }.getOrNull()?.response?.docs?.forEach {
            emit(
                SearchItem(type = SearchItemType.STORY,
                title = it.headline.main,
                description = it.byline.original,
                iconUrl = it.multimedia.firstOrNull { it.subtype == "thumbnail" }?.url?.let { "https://www.nytimes.com/$it" },
                uri = it.webUrl
            )
            )
        }
        finnhubSource.searchSymbol(query).also {
            println(it.exceptionOrNull())
        }.getOrNull()?.result?.forEach { found ->
            val info = finnhubSource.getInfoForSymbol(found.symbol).getOrNull()
            info?.let {
                emit(
                    SearchItem(
                        type = SearchItemType.TICKER,
                        title = it.name,
                        description = it.ticker,
                        iconUrl = it.logo,
                        uri = found.symbol
                    )
                )
            }
        }
    }
}