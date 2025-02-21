package com.yaabelozerov.superfinancer.common

import kotlinx.coroutines.flow.Flow

interface SearchAdapter {
    fun search(query: String): Flow<SearchItem>
}