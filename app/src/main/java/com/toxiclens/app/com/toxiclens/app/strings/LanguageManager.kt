package com.toxiclens.app.strings

object LanguageManager {

    fun isTurkish(language: String): Boolean {
        val normalizedLanguage = language
            .trim()
            .lowercase()

        return normalizedLanguage == "tr" ||
                normalizedLanguage.startsWith("tr-") ||
                normalizedLanguage.contains("türk") ||
                normalizedLanguage.contains("turkish")
    }
}