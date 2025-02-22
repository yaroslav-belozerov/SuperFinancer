package com.yaabelozerov.superfinancer.tickers

import android.app.Application
import com.yaabelozerov.superfinancer.common.Module

class TickerModule: Module() {
    override fun onCreate(application: Application) {
        app = application
    }

    companion object {
        private lateinit var app: Application

        internal val context by lazy { app.applicationContext }
    }
}