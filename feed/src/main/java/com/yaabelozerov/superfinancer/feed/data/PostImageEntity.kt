package com.yaabelozerov.superfinancer.feed.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
internal data class PostImageEntity(
    @PrimaryKey val path: String,
    val postId: Long
)
