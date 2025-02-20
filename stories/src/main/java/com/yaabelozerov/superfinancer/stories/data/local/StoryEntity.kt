package com.yaabelozerov.superfinancer.stories.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey val url: String,
    val timestampSaved: Long,
    val createdDate: String,
    val sectionKey: String,
    val byline: String,
    val title: String,
    val abstract: String,
    val imageUrl: String?
)
