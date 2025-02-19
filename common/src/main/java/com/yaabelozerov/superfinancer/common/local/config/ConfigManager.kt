package com.yaabelozerov.superfinancer.common.local.config

import android.content.Context
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

@ExperimentalSerializationApi
class ConfigManager(private val appContext: Context) {
    fun readConfig(): Config = Json.decodeFromStream<Config>(
        appContext.assets.open("sfconfig.json")
    )
}