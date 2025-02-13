package com.yaabelozerov.superfinancer.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.yaabelozerov.superfinancer.data.remote.nytimes.NytSource
import com.yaabelozerov.superfinancer.data.remote.nytimes.stories.NytStoryPagingSource
import com.yaabelozerov.superfinancer.data.remote.nytimes.stories.StoryPagingConfig
import com.yaabelozerov.superfinancer.data.remote.nytimes.stories.StoryPagingConfigDefaults
import com.yaabelozerov.superfinancer.domain.model.Section
import com.yaabelozerov.superfinancer.domain.model.Story
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoriesUseCase(
    private val remoteSource: NytSource = NytSource(),
) {
    fun createStoryFlow(section: String?): Flow<PagingData<Story>> {
        return Pager(
            PagingConfig(pageSize = StoryPagingConfigDefaults.defaultLimit)
        ) { NytStoryPagingSource(StoryPagingConfigDefaults.create(section = section.also { println("before create $section") })) }.flow.map {
            it.map {
                Story(
                    title = it.title,
                    description = it.abstract.ifBlank { it.subHeadline }.ifBlank { null },
                    author = it.byline,
                    photoUrl = it.multimedia.maxByOrNull { it.width }?.url,
                    sectionName = it.section
                )
            }
        }
    }

    suspend fun getSections(): List<Section> {
        return remoteSource.getSections().also { println(it.exceptionOrNull()) }.getOrNull()?.let {
            it.list.mapNotNull {
                if (it.section.isNotEmpty() && it.name.isNotEmpty()) {
                    Section(it.name, it.section)
                } else null
            }
        } ?: emptyList()
    }
}