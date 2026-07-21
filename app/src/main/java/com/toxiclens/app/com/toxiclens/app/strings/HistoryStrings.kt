package com.toxiclens.app.strings

data class HistoryStrings(
    val back: String,
    val title: String,

    val savedAnalyses: (Int) -> String,
    val shownAnalyses: (Int, Int) -> String,

    val freeLimitTitle: String,
    val freeLimitDescription: String,
    val upgradeToPremium: String,

    val searchLabel: String,
    val searchPlaceholder: String,

    val noAnalysisTitle: String,
    val noAnalysisDescription: String,

    val noMatchingTitle: String,
    val noMatchingDescription: String,

    val premiumFeatureTitle: String,
    val favoritesPremiumDescription: String,
    val upgrade: String,
    val cancel: String,

    val analysisNumber: (Int) -> String,
    val score: String,
    val toxicity: String,
    val unknown: String,
    val noSummary: String,

    val delete: String,
    val deleteAnalysisTitle: String,
    val deleteAnalysisDescription: String
)

object HistoryLanguage {

    fun get(language: String): HistoryStrings {
        return if (LanguageManager.isTurkish(language)) {
            turkish
        } else {
            english
        }
    }

    private val turkish = HistoryStrings(
        back = "Geri",
        title = "📜 Analiz Geçmişi",

        savedAnalyses = { count ->
            "$count kayıtlı analiz"
        },

        shownAnalyses = { visible, total ->
            "$total analizden $visible tanesi gösteriliyor"
        },

        freeLimitTitle = "🔒 Ücretsiz geçmiş sınırına ulaşıldı",
        freeLimitDescription =
            "Ücretsiz kullanıcılar yalnızca son 5 analizi görüntüleyebilir.",
        upgradeToPremium = "Premium'a Yükselt",

        searchLabel = "Geçmişte ara",
        searchPlaceholder = "Aile, patron, tarih, toksisite...",

        noAnalysisTitle = "Henüz analiz yok.",
        noAnalysisDescription =
            "Tamamlanan analizleriniz burada görünecek.",

        noMatchingTitle = "Eşleşen analiz bulunamadı.",
        noMatchingDescription =
            "Farklı bir arama terimi deneyin.",

        premiumFeatureTitle = "⭐ Premium Özellik",
        favoritesPremiumDescription =
            "Favoriler özelliği Read Between Premium ile kullanılabilir.",
        upgrade = "Yükselt",
        cancel = "İptal",

        analysisNumber = { index ->
            "Analiz #$index"
        },

        score = "Skor",
        toxicity = "Toksisite",
        unknown = "Belirsiz",
        noSummary = "Özet bulunmuyor.",

        delete = "🗑 Sil",
        deleteAnalysisTitle = "Analizi Sil",
        deleteAnalysisDescription =
            "Bu analizi silmek istediğinizden emin misiniz?"
    )

    private val english = HistoryStrings(
        back = "Back",
        title = "📜 Analysis History",

        savedAnalyses = { count ->
            "$count saved analyses"
        },

        shownAnalyses = { visible, total ->
            "$visible of $total analyses shown"
        },

        freeLimitTitle = "🔒 Free history limit reached",
        freeLimitDescription =
            "Free users can only view the latest 5 analyses.",
        upgradeToPremium = "Upgrade to Premium",

        searchLabel = "Search history",
        searchPlaceholder = "Family, Boss, date, toxicity...",

        noAnalysisTitle = "No analysis yet.",
        noAnalysisDescription =
            "Your completed analyses will appear here.",

        noMatchingTitle = "No matching analysis.",
        noMatchingDescription =
            "Try a different search term.",

        premiumFeatureTitle = "⭐ Premium Feature",
        favoritesPremiumDescription =
            "Favorites is available with Read Between Premium.",
        upgrade = "Upgrade",
        cancel = "Cancel",

        analysisNumber = { index ->
            "Analysis #$index"
        },

        score = "Score",
        toxicity = "Toxicity",
        unknown = "Unknown",
        noSummary = "No summary available.",

        delete = "🗑 Delete",
        deleteAnalysisTitle = "Delete Analysis",
        deleteAnalysisDescription =
            "Are you sure you want to delete this analysis?"
    )
}