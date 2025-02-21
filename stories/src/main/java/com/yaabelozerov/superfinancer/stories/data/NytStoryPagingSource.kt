package com.yaabelozerov.superfinancer.stories.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.yaabelozerov.superfinancer.stories.StoriesModule
import com.yaabelozerov.superfinancer.stories.data.local.StoriesDao
import com.yaabelozerov.superfinancer.stories.data.local.StoryEntity
import com.yaabelozerov.superfinancer.stories.data.remote.NytSource
import com.yaabelozerov.superfinancer.stories.data.remote.StoryDto
import com.yaabelozerov.superfinancer.stories.data.remote.StoryMultimediaDto

object StoryPagingDefaults {
    const val LIMIT = 15
    const val SECTION = "all"
    val EXCLUDE = listOf("admin")
}

private fun List<StoryEntity>.toDtos() = map {
    StoryDto(
        slugName = it.url,
        section = it.sectionKey,
        title = it.title,
        abstract = it.title,
        url = it.url,
        byline = it.byline,
        updatedDate = it.createdDate,
        createdDate = it.createdDate,
        firstPublishedDate = it.createdDate,
        subHeadline = it.abstract,
        multimedia = listOf(StoryMultimediaDto(
            url = it.imageUrl ?: "",
            width = 100L
        ))
    )
}

class NytStoryPagingSource(
    private val limit: Int = StoryPagingDefaults.LIMIT,
    private val section: String = StoryPagingDefaults.SECTION,
    private val source: NytSource = NytSource(),
    private val dao: StoriesDao = StoriesModule.storyCacheDao
) : PagingSource<Int, StoryDto>() {

    override fun getRefreshKey(state: PagingState<Int, StoryDto>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryDto> {
        val nextPage = params.key ?: 0
        val resp = source.getLatestStories(
            limit = limit, offset = nextPage, section = section
        ).getOrNull()
        if (resp != null) {
            val nextKey = (nextPage + limit).takeIf { resp.results.isNotEmpty() }
            return LoadResult.Page(data = resp.results,
                prevKey = null,
                nextKey = nextKey)
        } else {
            val paged = dao.getPaged(limit, nextPage).toDtos()
            val nextKey = (nextPage + limit).takeIf { paged.isNotEmpty() }
            return  LoadResult.Page(
                data = paged,
                prevKey = null,
                nextKey = nextKey
            )
        }
    }
}