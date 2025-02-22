package com.yaabelozerov.superfinancer.tickers

import com.yaabelozerov.superfinancer.common.SearchAdapter
import com.yaabelozerov.superfinancer.common.SearchItem
import com.yaabelozerov.superfinancer.common.SearchItemType
import com.yaabelozerov.superfinancer.tickers.data.FinnhubSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TickerSearchAdapter: SearchAdapter {
    private val finnhubSource = FinnhubSource()

    override fun search(query: String): Flow<SearchItem> = flow {
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