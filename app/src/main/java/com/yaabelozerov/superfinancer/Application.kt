package com.yaabelozerov.superfinancer

import android.app.Application
import com.yaabelozerov.superfinancer.common.CommonModule
import com.yaabelozerov.superfinancer.feed.FeedModule
import com.yaabelozerov.superfinancer.finance.FinanceModule
import com.yaabelozerov.superfinancer.stories.StoriesModule
import com.yaabelozerov.superfinancer.stories.StoriesSearchAdapter
import com.yaabelozerov.superfinancer.tickers.TickerModule
import com.yaabelozerov.superfinancer.tickers.TickerSearchAdapter

class Application: Application() {
    override fun onCreate() {
        modules.forEach {
            it.onCreate(this)
        }
        super.onCreate()
    }

    companion object {
        private val modules = listOf(
            CommonModule(), FinanceModule(), StoriesModule(), FeedModule(), TickerModule()
        )

        val searchAdapters = listOf(
            TickerSearchAdapter(), StoriesSearchAdapter()
        )
    }
}