package com.yaabelozerov.superfinancer.stories.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
internal interface StoriesDao {
    @Upsert
    suspend fun upsert(story: StoryEntity)

    @Query("SELECT * FROM stories WHERE url = :url")
    suspend fun getByUrl(url: String): StoryEntity

    @Query("SELECT * FROM stories WHERE sectionKey = :sectionName LIMIT :limit OFFSET :offset")
    suspend fun getPaged(limit: Int, offset: Int, sectionName: String): List<StoryEntity>

    @Query("SELECT * FROM stories LIMIT :limit OFFSET :offset")
    suspend fun getAllPaged(limit: Int, offset: Int): List<StoryEntity>

    @Query("DELETE FROM stories WHERE timestampSaved < :beforeTimestamp AND url NOT IN (:exclude)")
    suspend fun purgeCache(exclude: List<String>, beforeTimestamp: Long = System.currentTimeMillis() - CACHE_LIFETIME_MILLIS)

    companion object {
        private const val CACHE_LIFETIME_MILLIS = 6 * 60 * 60 * 1000 // 6 hours
    }
}