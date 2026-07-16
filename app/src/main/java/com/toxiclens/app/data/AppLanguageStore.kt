package com.toxiclens.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.languageDataStore by preferencesDataStore(
    name = "language_settings"
)

class AppLanguageStore(
    private val context: Context
) {
    private val languageKey = stringPreferencesKey("app_language")

    suspend fun saveLanguage(languageCode: String) {
        context.languageDataStore.edit { preferences ->
            preferences[languageKey] = languageCode
        }
    }

    suspend fun getLanguage(): String {
        val preferences = context.languageDataStore.data.first()
        return preferences[languageKey] ?: "tr"
    }
}