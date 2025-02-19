package com.yaabelozerov.superfinancer.stories.data

import androidx.paging.PagingSource
import androidx.paging.PagingState

object StoryPagingDefaults {
    const val LIMIT = 15
    const val SECTION = "all"
    val EXCLUDE = listOf("admin")
}

class NytStoryPagingSource(
    private val limit: Int = StoryPagingDefaults.LIMIT,
    private val section: String = StoryPagingDefaults.SECTION,
    private val source: NytSource = NytSource(),
) : PagingSource<Int, StoryDto>() {

    override fun getRefreshKey(state: PagingState<Int, StoryDto>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryDto> {
        val nextPage = params.key ?: 0
        val resp = source.getLatestStories(
            limit = limit, offset = nextPage, section = section
        )
        resp.getOrNull()?.let { dto ->
            return LoadResult.Page(data = dto.results,
                prevKey = null,
                nextKey = (nextPage + limit).takeIf { dto.results.isNotEmpty() })
        } ?: return LoadResult.Error(resp.exceptionOrNull() ?: Throwable("Unknown error"))
    }
}