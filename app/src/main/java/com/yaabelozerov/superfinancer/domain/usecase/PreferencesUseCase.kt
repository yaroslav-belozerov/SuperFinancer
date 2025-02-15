package com.yaabelozerov.superfinancer.domain.usecase

import com.yaabelozerov.superfinancer.Application
import com.yaabelozerov.superfinancer.data.local.datastore.DataStoreManager
import com.yaabelozerov.superfinancer.domain.model.Section
import com.yaabelozerov.superfinancer.ui.App
import kotlinx.coroutines.flow.first
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
class PreferencesUseCase() {
    private val dataStore = Application.dataStoreManager

    private val config = Application.configManager

    val defaultTickers by lazy {
        config.readConfig().defaultTickers
    }

    suspend fun getSections(): List<Section>? = dataStore.getValue(DataStoreManager.Keys.Strings.LAST_SECTIONS).first()?.let {
        Json.decodeFromString<List<Section>>(it)
    }

    suspend fun setSections(sections: List<Section>) {
        dataStore.setValue(DataStoreManager.Keys.Strings.LAST_SECTIONS, Json.encodeToString(sections))
    }
}