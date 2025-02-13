package com.yaabelozerov.superfinancer.data.remote.nytimes.stories

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.yaabelozerov.superfinancer.data.remote.nytimes.NytSource

data class StoryPagingConfig(
    var limit: Int,
    var section: String,
)

object StoryPagingConfigDefaults {
    const val defaultLimit = 15
    private const val defaultSection = "all"

    fun create(limit: Int = defaultLimit, section: String?): StoryPagingConfig {
        println("Creating config $limit $section")
        return StoryPagingConfig(limit = limit, section = section ?: defaultSection)
    }
}

class NytStoryPagingSource(
    private val config: StoryPagingConfig,
    private val source: NytSource = NytSource(),
) : PagingSource<Int, StoryDto>() {

    override fun getRefreshKey(state: PagingState<Int, StoryDto>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryDto> {
        val nextPage = params.key ?: 0
        val resp = source.getLatestStories(
            limit = config.limit, offset = nextPage, section = config.section
        )
        resp.getOrNull()?.let { dto ->
            return LoadResult.Page(data = dto.results,
                prevKey = null,
                nextKey = (nextPage + config.limit).takeIf { dto.results.isNotEmpty() })
        } ?: return LoadResult.Error(resp.exceptionOrNull() ?: Throwable("Unknown error"))
    }
}