package com.yaabelozerov.superfinancer

import android.app.Application
import com.yaabelozerov.superfinancer.common.CommonModule
import com.yaabelozerov.superfinancer.finance.FinanceModule

class Application: Application() {
    override fun onCreate() {
        CommonModule().onCreate(this)
        FinanceModule().onCreate(this)
        super.onCreate()
    }
}