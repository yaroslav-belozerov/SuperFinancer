package com.yaabelozerov.superfinancer.data.local.configuration

import android.content.Context
import io.ktor.utils.io.streams.asInput
import kotlinx.io.asInputStream
import kotlinx.io.asSource
import kotlinx.io.buffered
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.io.decodeFromSource

@ExperimentalSerializationApi
class ConfigManager(private val appContext: Context) {
    fun readConfig(): Config = Json.decodeFromSource<Config>(
        appContext.assets.open("sfconfig.json").asSource().buffered()
    )
}