package com.yaabelozerov.superfinancer.stories

import android.app.Application
import androidx.room.Room
import com.yaabelozerov.superfinancer.stories.data.local.StoriesDb
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class StoriesModule {
    fun onCreate(application: Application) {
        app = application
        MainScope().launch {
            storyCacheDao.purgeBefore(System.currentTimeMillis() - 6 * 60 * 60 * 1000)
        }
    }

    companion object {
        private lateinit var app: Application

        private val storyCacheDb by lazy {
            Room.databaseBuilder(app.applicationContext, StoriesDb::class.java, "stories.db").build()
        }
        val storyCacheDao by lazy {
            storyCacheDb.dao()
        }
    }
}