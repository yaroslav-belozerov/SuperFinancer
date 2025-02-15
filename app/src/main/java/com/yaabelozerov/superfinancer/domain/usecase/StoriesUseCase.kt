package com.yaabelozerov.superfinancer.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.yaabelozerov.superfinancer.data.remote.nytimes.NytSource
import com.yaabelozerov.superfinancer.data.remote.nytimes.stories.NytStoryPagingSource
import com.yaabelozerov.superfinancer.data.remote.nytimes.stories.SectionDto
import com.yaabelozerov.superfinancer.data.remote.nytimes.stories.StoryPagingDefaults
import com.yaabelozerov.superfinancer.data.remote.nytimes.stories.StoryPagingDefaults.EXCLUDE
import com.yaabelozerov.superfinancer.data.remote.nytimes.stories.StoryPagingDefaults.SECTION
import com.yaabelozerov.superfinancer.domain.model.Section
import com.yaabelozerov.superfinancer.domain.model.Story
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoriesUseCase(
    private val remoteSource: NytSource = NytSource(),
) {
    suspend fun getSections(interceptDto: (suspend (List<Section>) -> Unit)? = null): List<Section> {
        return remoteSource.getSections().getOrNull()?.let { dto ->
            dto.list.mapNotNull {
                if (it.section.isNotEmpty() && it.name.isNotEmpty()) {
                    Section(it.name, it.section)
                } else null
            }.filterNot { EXCLUDE.contains(it.key) }.also { interceptDto?.invoke(it) }
        } ?: emptyList()
    }

    fun createFlow(key: String?): Flow<PagingData<Story>> = Pager(
        PagingConfig(
            pageSize = StoryPagingDefaults.LIMIT,
            prefetchDistance = StoryPagingDefaults.LIMIT.div(2),
            enablePlaceholders = true
        ),
    ) {
        NytStoryPagingSource(
            section = key ?: SECTION
        )
    }.flow.map {
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