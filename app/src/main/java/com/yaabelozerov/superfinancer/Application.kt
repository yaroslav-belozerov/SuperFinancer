package com.yaabelozerov.superfinancer

import android.app.Application
import com.yaabelozerov.superfinancer.common.CommonModule
import com.yaabelozerov.superfinancer.common.Module
import com.yaabelozerov.superfinancer.feed.FeedModule
import com.yaabelozerov.superfinancer.finance.FinanceModule
import com.yaabelozerov.superfinancer.stories.StoriesModule

class Application: Application() {
    override fun onCreate() {
        modules.forEach {
            it.onCreate(this)
        }
        super.onCreate()
    }

    companion object {
        val modules = listOf(
            CommonModule(), FinanceModule(), StoriesModule(), FeedModule()
        )
    }
}