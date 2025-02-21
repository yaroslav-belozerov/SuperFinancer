package com.yaabelozerov.superfinancer.stories.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.yaabelozerov.superfinancer.stories.StoriesModule
import com.yaabelozerov.superfinancer.stories.data.local.StoriesDao
import com.yaabelozerov.superfinancer.stories.data.local.StoryEntity
import com.yaabelozerov.superfinancer.stories.data.remote.NytSource
import com.yaabelozerov.superfinancer.stories.data.remote.StoryDto
import com.yaabelozerov.superfinancer.stories.data.remote.StoryMultimediaDto
import com.yaabelozerov.superfinancer.stories.domain.Section

internal object StoryPagingDefaults {
    const val LIMIT = 15
    val SECTION = Section("all", "All")
    val EXCLUDE = listOf("admin")
}

private fun List<StoryEntity>.toDtos() = map {
    StoryDto(
        slugName = it.url,
        source = it.source,
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

internal class NytStoryPagingSource(
    private val limit: Int = StoryPagingDefaults.LIMIT,
    private val section: Section = StoryPagingDefaults.SECTION,
    private val source: NytSource = NytSource(),
    private val dao: StoriesDao = StoriesModule.storyCacheDao
) : PagingSource<Int, StoryDto>() {

    override fun getRefreshKey(state: PagingState<Int, StoryDto>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryDto> {
        val nextPage = params.key ?: 0
        val resp = source.getLatestStories(
            limit = limit, offset = nextPage, section = section.key
        ).getOrNull()
        if (resp != null) {
            val nextKey = (nextPage + limit).takeIf { resp.results.isNotEmpty() }
            return LoadResult.Page(data = resp.results,
                prevKey = null,
                nextKey = nextKey)
        } else {
            val paged = dao.run {
                if (section == StoryPagingDefaults.SECTION) {
                    getAllPaged(limit, nextPage)
                } else {
                    getPaged(limit, nextPage, section.name)
                }
            }.toDtos()
            val nextKey = (nextPage + limit).takeIf { paged.isNotEmpty() }
            return LoadResult.Page(
                data = paged,
                prevKey = null,
                nextKey = nextKey
            )
        }
    }
}