package com.yaabelozerov.superfinancer.common

import android.app.Application
import android.net.ConnectivityManager
import com.yaabelozerov.superfinancer.common.local.AuthenticationManager
import com.yaabelozerov.superfinancer.common.local.MediaManager
import com.yaabelozerov.superfinancer.common.local.config.ConfigManager
import com.yaabelozerov.superfinancer.common.local.config.DataStoreManager
import com.yaabelozerov.superfinancer.common.remote.NetworkCallback
import com.yaabelozerov.superfinancer.common.remote.networkRequest
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.ExperimentalSerializationApi

abstract class Module {
    abstract fun onCreate(application: Application)
}

class CommonModule: Module() {
    override fun onCreate(application: Application) {
        app = application
        val connectivityManager = application.getSystemService(ConnectivityManager::class.java)
        connectivityManager.registerNetworkCallback(networkRequest, NetworkCallback)
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
        private val configManager by lazy {
            ConfigManager(app.applicationContext)
        }

        @OptIn(ExperimentalSerializationApi::class)
        val config by lazy { configManager.readConfig() }

        val isNetworkAvailable = NetworkCallback.isConnected.asStateFlow()

        val authManager by lazy { AuthenticationManager() }
    }
}