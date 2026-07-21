package com.toxiclens.app.strings

data class HomeStrings(
    val welcome: String,
    val welcomeDescription: String,
    val slogan: String,
    val analyzeTitle: String,
    val analyzeBadge: String,
    val analyzeDescription: String,
    val pasteTitle: String,
    val pasteBadge: String,
    val pasteDescription: String,
    val historyTitle: String,
    val historyBadge: String,
    val historyDescription: String,
    val premiumTitle: String,
    val premiumBadge: String,
    val premiumDescription: String,
    val privacyTitle: String,
    val privacyDescription: String
)

object HomeLanguage {

    fun get(language: String): HomeStrings {
        return if (LanguageManager.isTurkish(language)) {
            turkish
        } else {
            english
        }
    }

    private val turkish = HomeStrings(
        welcome = "Hoş geldiniz 👋",
        welcomeDescription = "Konuşmaları birlikte anlayalım.",
        slogan = "Kelimelerin gerçekte ne anlattığını keşfedin.",
        analyzeTitle = "Ekran Görüntüsünü Analiz Et",
        analyzeBadge = "Yapay Zekâ",
        analyzeDescription = "WhatsApp, Instagram, SMS veya Messenger ekran görüntülerini analiz edin.",
        pasteTitle = "Konuşmayı Yapıştır",
        pasteBadge = "Hızlı",
        pasteDescription = "Kopyalanmış mesajları yapıştırın ve iletişimi yapay zekâya analiz ettirin.",
        historyTitle = "Önceki Analizler",
        historyBadge = "Kayıtlı",
        historyDescription = "Kaydedilmiş analiz raporlarınızı ve geçmişinizi görüntüleyin.",
        premiumTitle = "Read Between Premium",
        premiumBadge = "Yükselt",
        premiumDescription = "20 ekran görüntüsü, sınırsız geçmiş, PDF dışa aktarma ve daha fazlasını açın.",
        privacyTitle = "Gizliliğiniz önceliğimizdir",
        privacyDescription = "Konuşmalarınız izniniz olmadan hiçbir zaman saklanmaz veya paylaşılmaz."
    )

    private val english = HomeStrings(
        welcome = "Welcome 👋",
        welcomeDescription = "Let's understand conversations.",
        slogan = "Understand what words really mean.",
        analyzeTitle = "Analyze Screenshot",
        analyzeBadge = "AI Powered",
        analyzeDescription = "Analyze WhatsApp, Instagram, SMS or Messenger screenshots.",
        pasteTitle = "Paste Conversation",
        pasteBadge = "Fast",
        pasteDescription = "Paste copied messages and let AI analyze the communication.",
        historyTitle = "Previous Analyses",
        historyBadge = "Saved",
        historyDescription = "View your saved analysis reports and history.",
        premiumTitle = "Read Between Premium",
        premiumBadge = "Upgrade",
        premiumDescription = "Unlock 20 screenshots, unlimited history, PDF export and more.",
        privacyTitle = "Your privacy is our priority",
        privacyDescription = "Your conversations are never stored or shared without your permission."
    )
}