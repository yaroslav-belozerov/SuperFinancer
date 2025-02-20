package com.yaabelozerov.superfinancer.feed

import android.app.Application
import androidx.room.Room
import com.yaabelozerov.superfinancer.common.Module
import com.yaabelozerov.superfinancer.feed.data.PostDao
import com.yaabelozerov.superfinancer.feed.data.PostDb

class FeedModule: Module() {
    override fun onCreate(application: Application) {
        app = application
    }

    companion object {
        private lateinit var app: Application

        val postDb by lazy {
            Room.databaseBuilder(app.applicationContext, PostDb::class.java, "posts.db").build()
        }
        val postDao by lazy { postDb.dao() }
    }
}