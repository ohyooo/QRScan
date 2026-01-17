package com.ohyooo.qrscan.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json


val KEY_LIST = stringPreferencesKey("list")

val Context.ds: DataStore<Preferences> by preferencesDataStore(name = "history")

suspend fun Context.addHistory(result: String): List<String> {
    val value = getHistories().apply {
        if (result.isBlank()) {
            return this
        }
        if (contains(result)) {
            removeAt(indexOf(result))
        }
        if (size > 20) {
            removeFirstOrNull()
        }

        if (result == lastOrNull()) return this
        add(result)
    }

    if (result.isNotBlank()) {
        ds.edit { settings ->
            settings[KEY_LIST] = Json.encodeToString(value)
        }
    }
    return value
}

private fun Context.getHistories(): MutableList<String> {
    var json = "[]"

    runBlocking {
        ds.data.map { settings ->
            val listString = settings[KEY_LIST] ?: return@map
            json = listString
        }.first()
    }
    return try {
        Json.decodeFromString(json)
    } catch (e: Exception) {
        e.printStackTrace()
        mutableListOf()
    }
}

suspend fun Context.clearHistory() {
    ds.edit { settings ->
        settings[KEY_LIST] = "[]"
    }
}
