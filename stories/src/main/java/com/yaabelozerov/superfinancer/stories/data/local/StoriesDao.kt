package com.yaabelozerov.superfinancer.stories.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface StoriesDao {
    @Upsert
    suspend fun upsert(story: StoryEntity)

    @Query("SELECT * FROM stories WHERE slug = :slug")
    suspend fun get(slug: String): StoryEntity

    @Query("SELECT * FROM stories LIMIT :limit OFFSET :offset")
    suspend fun getPaged(limit: Int, offset: Int): List<StoryEntity>

    @Query("DELETE FROM stories WHERE timestampSaved < :timestamp")
    suspend fun purgeBefore(timestamp: Long)
}