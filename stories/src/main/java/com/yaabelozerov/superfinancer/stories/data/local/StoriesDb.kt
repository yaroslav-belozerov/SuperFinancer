package com.yaabelozerov.superfinancer.stories.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [StoryEntity::class], version = 1, exportSchema = false)
internal abstract class StoriesDb: RoomDatabase() {
    abstract fun dao(): StoriesDao
}