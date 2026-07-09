package com.toxiclens.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Context.dataStore by preferencesDataStore(name = "history_store")

class HistoryStore(
    private val context: Context
) {
    private val historyKey = stringPreferencesKey("analysis_history")

    suspend fun saveAnalysis(
        result: String,
        conversationType: String
    ) {
        val oldData = getHistoryRaw()

        val date = SimpleDateFormat(
            "dd.MM.yyyy HH:mm",
            Locale.getDefault()
        ).format(Date())

        val newItem =
            "TYPE::${conversationType.replace("|||", " ")}###DATE::${date}###RESULT::${result.replace("|||", " ")}"

        val updated = if (oldData.isBlank()) {
            newItem
        } else {
            "$newItem|||$oldData"
        }

        context.dataStore.edit { prefs ->
            prefs[historyKey] = updated
        }
    }

    suspend fun getHistory(): List<String> {
        val raw = getHistoryRaw()
        if (raw.isBlank()) return emptyList()

        return raw.split("|||")
    }

    suspend fun deleteAnalysis(itemToDelete: String) {
        val currentList = getHistory().toMutableList()
        currentList.remove(itemToDelete)

        val updated = currentList.joinToString("|||")

        context.dataStore.edit { prefs ->
            prefs[historyKey] = updated
        }
    }

    private suspend fun getHistoryRaw(): String {
        val prefs = context.dataStore.data.first()
        return prefs[historyKey] ?: ""
    }
}