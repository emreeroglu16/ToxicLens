package com.toxiclens.app.strings

data class AnalyzeStrings(
    val back: String,
    val title: String,
    val description: String,
    val addScreenshots: String,
    val selected: String,
    val screenshots: String,
    val premiumPlan: String,
    val freePlan: String,
    val freeLimitReached: String,
    val noScreenshotsTitle: String,
    val noScreenshotsDescription: String,
    val continueButton: String,
    val screenshot: String,
    val ready: String,
    val remove: String,
    val premiumTitle: String,
    val premiumDescription: String,
    val imageContentDescription: String
)

object AnalyzeLanguage {

    fun get(language: String): AnalyzeStrings {
        return when (language.lowercase()) {
            "tr", "turkish", "türkçe" -> turkish
            else -> english
        }
    }

    private val english = AnalyzeStrings(
        back = "← Back",
        title = "Conversation Screenshots",
        description =
            "Select screenshots from the same conversation in the correct order.",
        addScreenshots = "+ Add Screenshots",
        selected = "Selected",
        screenshots = "screenshots",
        premiumPlan = "Premium Plan",
        freePlan = "Free Plan",
        freeLimitReached =
            "Free limit reached. Premium allows up to 20 screenshots.",
        noScreenshotsTitle = "No screenshots selected yet",
        noScreenshotsDescription =
            "Choose up to 2 screenshots for free.",
        continueButton = "Continue",
        screenshot = "Screenshot",
        ready = "✓ Ready",
        remove = "Remove",
        premiumTitle = "⭐ Premium",
        premiumDescription =
            "Analyze up to 20 screenshots, access detailed reports, unlimited history and PDF export.",
        imageContentDescription = "Conversation screenshot"
    )

    private val turkish = AnalyzeStrings(
        back = "← Geri",
        title = "Konuşma Ekran Görüntüleri",
        description =
            "Aynı konuşmaya ait ekran görüntülerini doğru sırayla seçin.",
        addScreenshots = "+ Ekran Görüntüsü Ekle",
        selected = "Seçilenler",
        screenshots = "ekran görüntüsü",
        premiumPlan = "Premium Plan",
        freePlan = "Ücretsiz Plan",
        freeLimitReached =
            "Ücretsiz limite ulaşıldı. Premium ile 20 ekran görüntüsüne kadar analiz yapabilirsiniz.",
        noScreenshotsTitle = "Henüz ekran görüntüsü seçilmedi",
        noScreenshotsDescription =
            "Ücretsiz olarak en fazla 2 ekran görüntüsü seçebilirsiniz.",
        continueButton = "Devam Et",
        screenshot = "Ekran Görüntüsü",
        ready = "✓ Hazır",
        remove = "Kaldır",
        premiumTitle = "⭐ Premium",
        premiumDescription =
            "20 ekran görüntüsüne kadar analiz, ayrıntılı raporlar, sınırsız geçmiş ve PDF dışa aktarma özelliklerini kullanın.",
        imageContentDescription = "Konuşma ekran görüntüsü"
    )
}