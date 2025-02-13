package com.yaabelozerov.superfinancer.domain.usecase

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.yaabelozerov.superfinancer.data.remote.nytimes.NytSource
import com.yaabelozerov.superfinancer.data.remote.nytimes.stories.StoryDto

data class StoryPagingConfig(
    var limit: Int,
    var section: String
)

class StoryUseCase(private val config: StoryPagingConfig = DefaultConfig, private val source: NytSource = NytSource()): PagingSource<Int, StoryDto>() {
    override fun getRefreshKey(state: PagingState<Int, StoryDto>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryDto> {
        val nextPage = params.key ?: 0
        val resp = source.getLatestStories(limit = config.limit, offset = nextPage, section = config.section)
        println(resp)
        resp.getOrNull()?.let { dto ->
            return LoadResult.Page(
                data = dto.results,
                prevKey = null,
                nextKey = (nextPage + config.limit).takeIf { dto.results.isNotEmpty() }
            )
        } ?: return LoadResult.Error(resp.exceptionOrNull() ?: Throwable("Unknown error"))
    }

    companion object {
        val DefaultConfig = StoryPagingConfig(15, "all")
    }
}