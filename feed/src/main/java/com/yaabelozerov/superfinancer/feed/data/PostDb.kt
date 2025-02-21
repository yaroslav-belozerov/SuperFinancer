package com.yaabelozerov.superfinancer.feed.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PostEntity::class, PostImageEntity::class], version = 1, exportSchema = false)
internal abstract class PostDb: RoomDatabase() {
    abstract fun dao(): PostDao
}