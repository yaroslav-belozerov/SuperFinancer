package com.yaabelozerov.superfinancer.stories.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface StoriesDao {
    @Upsert
    suspend fun upsert(story: StoryEntity)

    @Query("SELECT * FROM stories WHERE slug = :slug")
    suspend fun get(slug: String): StoryEntity

    @Query("DELETE FROM stories WHERE timestampSaved < :timestamp")
    suspend fun purgeBefore(timestamp: Long)
}