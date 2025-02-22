package com.yaabelozerov.superfinancer.search

import com.yaabelozerov.superfinancer.Application
import com.yaabelozerov.superfinancer.common.SearchAdapter
import com.yaabelozerov.superfinancer.common.SearchItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge

class SearchUseCase(private val adapters: List<SearchAdapter> = Application.searchAdapters) {
    fun search(query: String): Flow<SearchItem> =
        adapters.map { it.search(query) }.merge()
}