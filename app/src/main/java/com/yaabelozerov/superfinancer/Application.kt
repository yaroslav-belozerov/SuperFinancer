package com.yaabelozerov.superfinancer

import android.app.Application
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yaabelozerov.superfinancer.data.local.configuration.ConfigManager
import com.yaabelozerov.superfinancer.data.local.datastore.DataStoreManager
import com.yaabelozerov.superfinancer.data.local.media.MediaManager
import com.yaabelozerov.superfinancer.data.local.room.finance.FinanceDb
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

        private val financeDb by lazy {
            Room.databaseBuilder(app.applicationContext, FinanceDb::class.java, "finance.db").build()
        }
        val financeDao by lazy { financeDb.dao() }

        val mediaManager by lazy {
            MediaManager(app.applicationContext)
        }
    }
}