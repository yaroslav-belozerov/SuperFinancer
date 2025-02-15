package com.yaabelozerov.superfinancer

import android.app.Application
import android.content.Context
import com.yaabelozerov.superfinancer.data.local.configuration.ConfigManager
import com.yaabelozerov.superfinancer.data.local.datastore.DataStoreManager
import kotlinx.serialization.ExperimentalSerializationApi

class Application: Application() {
    override fun onCreate() {
        super.onCreate()
        app = this
    }

    companion object {
        private lateinit var app: Application

        val dataStoreManager by lazy {
            DataStoreManager(app.applicationContext)
        }

        @OptIn(ExperimentalSerializationApi::class)
        val configManager by lazy {
            ConfigManager(app.applicationContext)
        }
    }
}