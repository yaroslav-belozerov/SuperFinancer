package com.yaabelozerov.superfinancer.stories.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface StoriesDao {
    @Upsert
    suspend fun upsert(story: StoryEntity)

    @Query("SELECT * FROM stories WHERE url = :url")
    suspend fun getByUrl(url: String): StoryEntity

    @Query("SELECT * FROM stories LIMIT :limit OFFSET :offset")
    suspend fun getPaged(limit: Int, offset: Int): List<StoryEntity>

    @Query("DELETE FROM stories WHERE timestampSaved < :timestamp AND url NOT IN (:exclude)")
    suspend fun purgeBefore(timestamp: Long, exclude: List<String>)
}