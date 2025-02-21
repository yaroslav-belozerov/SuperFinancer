package com.yaabelozerov.superfinancer.feed

import android.app.Application
import androidx.room.Room
import com.yaabelozerov.superfinancer.common.Module
import com.yaabelozerov.superfinancer.feed.data.PostDb
import com.yaabelozerov.superfinancer.stories.StoriesModule
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class FeedModule : Module() {
    override fun onCreate(application: Application) {
        app = application
        MainScope().launch {
            StoriesModule.postAdapter.purgeCache(postDao.getAllUsedArticleIds())
        }
    }

    companion object {
        private lateinit var app: Application

        internal val postDb by lazy {
            Room.databaseBuilder(app.applicationContext, PostDb::class.java, "posts.db").build()
        }
        internal val postDao by lazy { postDb.dao() }
    }
}