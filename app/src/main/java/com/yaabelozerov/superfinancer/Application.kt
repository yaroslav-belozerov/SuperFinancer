package com.yaabelozerov.superfinancer

import android.app.Application
import com.yaabelozerov.superfinancer.common.CommonModule
import com.yaabelozerov.superfinancer.finance.FinanceModule
import com.yaabelozerov.superfinancer.stories.StoriesModule

class Application: Application() {
    override fun onCreate() {
        CommonModule().onCreate(this)
        FinanceModule().onCreate(this)
        StoriesModule().onCreate(this)
        super.onCreate()
    }
}