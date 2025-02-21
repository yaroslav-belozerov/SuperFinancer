package com.yaabelozerov.superfinancer.stories.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.yaabelozerov.superfinancer.common.CommonModule
import com.yaabelozerov.superfinancer.common.local.config.DataStoreManager
import com.yaabelozerov.superfinancer.common.util.format
import com.yaabelozerov.superfinancer.stories.StoriesModule
import com.yaabelozerov.superfinancer.stories.data.local.StoryEntity
import com.yaabelozerov.superfinancer.stories.data.StoryPagingDefaults.EXCLUDE
import com.yaabelozerov.superfinancer.stories.data.StoryPagingDefaults.SECTION
import com.yaabelozerov.superfinancer.stories.data.remote.NytSource
import com.yaabelozerov.superfinancer.stories.data.NytStoryPagingSource
import com.yaabelozerov.superfinancer.stories.data.StoryPagingDefaults
import com.yaabelozerov.superfinancer.stories.data.local.StoriesDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class StoriesUseCase(
    private val remoteSource: NytSource = NytSource(),
    private val dao: StoriesDao = StoriesModule.storyCacheDao
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
            val entity = StoryEntity(
                timestampSaved = System.currentTimeMillis(),
                title = it.title,
                abstract = it.abstract.ifBlank { it.subHeadline },
                url = it.url,
                imageUrl = it.multimedia.maxByOrNull { it.width }?.url,
                createdDate = it.createdDate,
                sectionKey = it.section,
                byline = it.byline
            )
            StoriesModule.storyCacheDao.upsert(entity)
            Story(
                title = it.title,
                description = it.abstract.ifBlank { it.subHeadline }.ifBlank { null },
                author = it.byline,
                link = it.url,
                photoUrl = it.multimedia.maxByOrNull { it.width }?.url,
                sectionName = it.section,
                date = LocalDateTime.ofInstant(
                    Instant.parse(it.updatedDate.ifBlank { it.firstPublishedDate.ifBlank { it.createdDate } }),
                    ZoneId.systemDefault()
                ).format()
            )
        }
    }

    suspend fun getCachedStoryByUrl(url: String) = dao.getByUrl(url).run {
        Story(
            title = title,
            description = abstract,
            author = byline,
            link = this.url,
            photoUrl = imageUrl,
            sectionName = sectionKey,
            date = createdDate
        )
    }

    companion object {
        fun getSavedSections(): Flow<List<Section>?> =
            CommonModule.dataStoreManager.getValue(DataStoreManager.Keys.Strings.LAST_SECTIONS)
                .map {
                    it?.let {
                        Json.decodeFromString(it)
                    }
                }

        suspend fun setSavedSections(list: List<Section>) = CommonModule.dataStoreManager.setValue(
            DataStoreManager.Keys.Strings.LAST_SECTIONS, Json.encodeToString(list)
        )
    }
}