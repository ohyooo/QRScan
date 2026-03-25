package com.ohyooo.qrscan.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private const val MAX_HISTORY_SIZE = 20

private val historyKey = stringPreferencesKey("list")

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "history")

class HistoryRepository(private val context: Context) {

    val history: Flow<List<String>> = context.dataStore.data.map { preferences ->
        decodeHistory(preferences[historyKey])
    }

    suspend fun add(entry: String) {
        val normalizedEntry = entry.trim()
        if (normalizedEntry.isBlank()) return

        context.dataStore.edit { preferences ->
            val updatedHistory = decodeHistory(preferences[historyKey])
                .toMutableList()
                .apply {
                    remove(normalizedEntry)
                    add(normalizedEntry)
                    while (size > MAX_HISTORY_SIZE) {
                        removeAt(0)
                    }
                }

            preferences[historyKey] = Json.encodeToString(updatedHistory)
        }
    }

    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences[historyKey] = "[]"
        }
    }

    private fun decodeHistory(encodedValue: String?): List<String> = try {
        Json.decodeFromString<List<String>>(encodedValue ?: "[]")
    } catch (_: Exception) {
        emptyList()
    }
}
