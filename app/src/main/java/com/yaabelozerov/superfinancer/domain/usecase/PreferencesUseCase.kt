package com.yaabelozerov.superfinancer.domain.usecase

import android.content.Context
import com.yaabelozerov.superfinancer.Application
import com.yaabelozerov.superfinancer.data.local.datastore.DataStoreManager
import com.yaabelozerov.superfinancer.domain.model.Section
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PreferencesUseCase() {
    private val manager = Application.getDataStoreManager()

    suspend fun getSections(): List<Section>? = manager.getValue(DataStoreManager.Keys.Strings.LAST_SECTIONS).first()?.let {
        Json.decodeFromString<List<Section>>(it)
    }

    suspend fun setSections(sections: List<Section>) {
        manager.setValue(DataStoreManager.Keys.Strings.LAST_SECTIONS, Json.encodeToString(sections))
    }
}