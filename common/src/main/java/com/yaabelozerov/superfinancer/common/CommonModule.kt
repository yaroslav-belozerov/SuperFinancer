package com.yaabelozerov.superfinancer.common

import android.app.Application
import android.content.ContextParams
import com.yaabelozerov.superfinancer.common.local.MediaManager
import com.yaabelozerov.superfinancer.common.local.config.ConfigManager
import com.yaabelozerov.superfinancer.common.local.config.DataStoreManager
import kotlinx.serialization.ExperimentalSerializationApi

open class CommonModule {
    fun onCreate(application: Application) {
        app = application
    }
    companion object {
        private lateinit var app: Application

        val mediaManager by lazy {
            MediaManager(app.applicationContext)
        }

        val dataStoreManager by lazy {
            DataStoreManager(app.applicationContext)
        }

        @OptIn(ExperimentalSerializationApi::class)
        val configManager by lazy {
            ConfigManager(app.applicationContext)
        }
    }
}