package com.toxiclens.app.strings

data class ConversationTypeStrings(
    val back: String,
    val title: String,
    val description: String,
    val relationship: String,
    val friend: String,
    val family: String,
    val boss: String,
    val coworker: String,
    val customer: String,
    val other: String,
    val analyzeConversation: String,
    val analyzing: String,
    val geminiAnalyzing: String,
    val analysisError: String
)

object ConversationTypeLanguage {

    fun get(language: String): ConversationTypeStrings {
        return if (LanguageManager.isTurkish(language)) {
            turkish
        } else {
            english
        }
    }

    private val turkish = ConversationTypeStrings(
        back = "Geri",
        title = "Bu konuşma kiminle?",
        description = "Analize başlamadan önce bir seçenek belirleyin.",
        relationship = "Romantik İlişki",
        friend = "Arkadaş",
        family = "Aile",
        boss = "Yönetici",
        coworker = "İş Arkadaşı",
        customer = "Müşteri",
        other = "Diğer",
        analyzeConversation = "Konuşmayı Analiz Et",
        analyzing = "Analiz ediliyor...",
        geminiAnalyzing = "Gemini seçilen kategoriye göre inceliyor.",
        analysisError = "Analiz sırasında bir hata oluştu."
    )

    private val english = ConversationTypeStrings(
        back = "Back",
        title = "Who is this conversation with?",
        description = "Select one option before starting the analysis.",
        relationship = "Relationship",
        friend = "Friend",
        family = "Family",
        boss = "Boss",
        coworker = "Coworker",
        customer = "Customer",
        other = "Other",
        analyzeConversation = "Analyze Conversation",
        analyzing = "Analyzing...",
        geminiAnalyzing = "Gemini is reviewing the selected category.",
        analysisError = "An error occurred during the analysis."
    )
}