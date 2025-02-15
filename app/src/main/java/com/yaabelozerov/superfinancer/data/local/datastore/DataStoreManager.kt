package com.yaabelozerov.superfinancer.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class DataStoreManager(context: Context) {
    private val Context.dataStore by preferencesDataStore("preferences")
    private val dataStore = context.dataStore

    fun getValue(key: Strings) = dataStore.data.map {
        it[key.key]
    }

    suspend fun setValue(key: Strings, value: String) {
        dataStore.edit { it[key.key] = value }
    }

    companion object Keys {
        enum class Strings(internal val key: Preferences.Key<String>) {
            LAST_SECTIONS(stringPreferencesKey("last_sections"))
        }
    }
}