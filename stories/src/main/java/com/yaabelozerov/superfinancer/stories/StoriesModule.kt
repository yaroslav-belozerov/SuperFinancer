package com.yaabelozerov.superfinancer.stories

import android.app.Application
import androidx.room.Room
import com.yaabelozerov.superfinancer.common.Module
import com.yaabelozerov.superfinancer.stories.data.local.StoriesDb
import com.yaabelozerov.superfinancer.stories.data.local.StoryEntity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class StoriesModule: Module() {
    override fun onCreate(application: Application) {
        app = application
    }

    companion object {
        private lateinit var app: Application

        private val storyCacheDb by lazy {
            Room.databaseBuilder(app.applicationContext, StoriesDb::class.java, "stories.db").build()
        }
        internal val storyCacheDao by lazy {
            storyCacheDb.dao()
        }

        val postAdapter by lazy { StoriesToPostAdapter() }
    }
}