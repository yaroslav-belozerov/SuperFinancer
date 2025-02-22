package com.yaabelozerov.superfinancer.common.local.config

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreManager(context: Context) {
    private val Context.dataStore by preferencesDataStore("preferences")
    private val dataStore = context.dataStore

    fun getValue(key: Strings) = dataStore.data.map {
        it[key.key]
    }

    fun isKeySet(key: Strings) = getValue(key).map { it != null }

    internal suspend fun setValue(key: Strings, value: String) {
        dataStore.edit { it[key.key] = value }
    }

    suspend fun setLastRoute(route: String) = setValue(Keys.Strings.LAST_ROUTE, route)
    suspend fun setSections(sectionString: String) = setValue(Keys.Strings.LAST_SECTIONS, sectionString)

    companion object Keys {
        enum class Strings(internal val key: Preferences.Key<String>) {
            LAST_SECTIONS(stringPreferencesKey("last_sections")),
            LAST_ROUTE(stringPreferencesKey("last_route")),
            FAVOURITE_PASSWORD_HASH(stringPreferencesKey("favourite_password")),
        }
    }
}