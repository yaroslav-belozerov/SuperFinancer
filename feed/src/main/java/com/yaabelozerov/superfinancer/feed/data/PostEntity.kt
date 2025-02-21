package com.yaabelozerov.superfinancer.feed.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val contents: String,
    val articleId: String?
)
