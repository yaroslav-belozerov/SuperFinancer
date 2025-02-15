package com.yaabelozerov.superfinancer

import android.app.Application
import android.content.Context
import com.yaabelozerov.superfinancer.data.local.datastore.DataStoreManager

class Application: Application() {
    override fun onCreate() {
        super.onCreate()
        app = this
    }

    companion object {
        private lateinit var app: Application

        fun getDataStoreManager() = DataStoreManager(app.applicationContext)
    }
}