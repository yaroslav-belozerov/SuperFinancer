package com.yaabelozerov.superfinancer.stories

import com.yaabelozerov.superfinancer.common.SearchAdapter
import com.yaabelozerov.superfinancer.common.SearchItem
import com.yaabelozerov.superfinancer.common.SearchItemType
import com.yaabelozerov.superfinancer.stories.data.local.StoryEntity
import com.yaabelozerov.superfinancer.stories.data.remote.NytSource
import com.yaabelozerov.superfinancer.stories.domain.StoriesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class StoriesSearchAdapter : SearchAdapter {
    private val storiesSource by lazy { NytSource() }
    private val storiesDao by lazy { StoriesModule.storyCacheDao }

    override fun search(query: String): Flow<SearchItem> = flow {
        storiesSource.searchStories(query).also {
            println(it.exceptionOrNull())
        }.getOrNull()?.response?.docs?.forEach { doc ->
            storiesDao.upsert(StoryEntity(timestampSaved = System.currentTimeMillis(),
                title = doc.headline.main,
                abstract = doc.snippet,
                source = doc.source,
                url = doc.webUrl,
                imageUrl = doc.multimedia.firstOrNull { it.subtype == "thumbnail" }?.url?.let { "$IMAGE_PREFIX$it" },
                createdDate = doc.createdDate,
                sectionKey = StoriesUseCase.getSavedSections().first()?.firstOrNull { it.name == doc.sectionName }?.name ?: "all",
                byline = doc.byline.original
            )
            )
            emit(SearchItem(type = SearchItemType.STORY,
                title = doc.headline.main,
                description = doc.byline.original,
                iconUrl = doc.multimedia.firstOrNull { it.subtype == "thumbnail" }?.url?.let { "$IMAGE_PREFIX$it" },
                uri = doc.webUrl
            )
            )
        }
    }

    companion object {
        private const val IMAGE_PREFIX = "https://www.nytimes.com/"
    }
}