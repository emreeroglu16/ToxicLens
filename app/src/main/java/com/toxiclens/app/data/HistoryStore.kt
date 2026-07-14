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
            "TYPE::${conversationType.replace("|||", " ")}" +
                    "###DATE::$date" +
                    "###FAVORITE::false" +
                    "###RESULT::${result.replace("|||", " ")}"

        val updated = if (oldData.isBlank()) {
            newItem
        } else {
            "$newItem|||$oldData"
        }

        saveRaw(updated)
    }

    suspend fun getHistory(): List<String> {
        val raw = getHistoryRaw()

        if (raw.isBlank()) {
            return emptyList()
        }

        return raw.split("|||")
    }

    suspend fun deleteAnalysis(itemToDelete: String) {
        val updated = getHistory()
            .filter { it != itemToDelete }
            .joinToString("|||")

        saveRaw(updated)
    }

    suspend fun toggleFavorite(itemToUpdate: String) {
        val updated = getHistory().map { item ->
            if (item == itemToUpdate) {
                changeFavoriteValue(item)
            } else {
                item
            }
        }

        saveRaw(updated.joinToString("|||"))
    }

    private fun changeFavoriteValue(item: String): String {
        val favoriteTag = "FAVORITE::"
        val favoriteStart = item.indexOf(favoriteTag)

        if (favoriteStart == -1) {
            val resultTag = "###RESULT::"

            return if (item.contains(resultTag)) {
                item.replace(
                    resultTag,
                    "###FAVORITE::true$resultTag"
                )
            } else {
                "$item###FAVORITE::true"
            }
        }

        val valueStart = favoriteStart + favoriteTag.length
        val valueEnd = item.indexOf("###", valueStart)
            .takeIf { it != -1 }
            ?: item.length

        val currentValue = item.substring(valueStart, valueEnd)
        val newValue = if (currentValue.equals("true", true)) {
            "false"
        } else {
            "true"
        }

        return item.replaceRange(
            valueStart,
            valueEnd,
            newValue
        )
    }

    private suspend fun saveRaw(value: String) {
        context.dataStore.edit { preferences ->
            preferences[historyKey] = value
        }
    }

    private suspend fun getHistoryRaw(): String {
        val preferences = context.dataStore.data.first()
        return preferences[historyKey] ?: ""
    }
}