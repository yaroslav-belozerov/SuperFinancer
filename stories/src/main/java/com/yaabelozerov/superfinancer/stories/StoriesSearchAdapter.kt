package com.yaabelozerov.superfinancer.stories

import com.yaabelozerov.superfinancer.common.SearchAdapter
import com.yaabelozerov.superfinancer.common.SearchItem
import com.yaabelozerov.superfinancer.common.SearchItemType
import com.yaabelozerov.superfinancer.stories.data.local.StoryEntity
import com.yaabelozerov.superfinancer.stories.data.remote.NytSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StoriesSearchAdapter : SearchAdapter {
    private val storiesSource by lazy { NytSource() }
    private val storiesDao by lazy { StoriesModule.storyCacheDao }

    override fun search(query: String): Flow<SearchItem> = flow {
        storiesSource.searchStories(query).also {
            println(it.exceptionOrNull())
        }.getOrNull()?.response?.docs?.forEach {
            storiesDao.upsert(StoryEntity(timestampSaved = System.currentTimeMillis(),
                title = it.headline.main,
                abstract = it.snippet,
                url = it.webUrl,
                imageUrl = it.multimedia.firstOrNull { it.subtype == "thumbnail" }?.url?.let { "https://www.nytimes.com/$it" },
                createdDate = "",
                sectionKey = "",
                byline = it.byline.original
            )
            )
            emit(SearchItem(type = SearchItemType.STORY,
                title = it.headline.main,
                description = it.byline.original,
                iconUrl = it.multimedia.firstOrNull { it.subtype == "thumbnail" }?.url?.let { "https://www.nytimes.com/$it" },
                uri = it.webUrl
            )
            )
        }
    }
}