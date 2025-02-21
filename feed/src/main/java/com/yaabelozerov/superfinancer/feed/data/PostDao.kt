package com.yaabelozerov.superfinancer.feed.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
internal interface PostDao {
    @Query("SELECT * FROM posts LEFT OUTER JOIN images ON postId = posts.id")
    fun getAllPosts(): Flow<Map<PostEntity, List<PostImageEntity>>>

    @Query("SELECT articleId FROM posts WHERE articleId IS NOT NULL")
    suspend fun getAllUsedArticleIds(): List<String>

    @Upsert
    suspend fun createPost(postEntity: PostEntity): Long

    @Upsert
    suspend fun createImageRecord(list: List<PostImageEntity>)
}