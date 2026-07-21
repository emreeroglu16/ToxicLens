package com.toxiclens.app.strings

data class PremiumStrings(
    val back: String,
    val activeDescription: String,
    val inactiveDescription: String,

    val monthly: String,
    val monthlyPeriod: String,
    val chooseMonthly: String,

    val yearly: String,
    val yearlyPeriod: String,
    val chooseYearly: String,
    val mostPopular: String,
    val yearlySaving: String,

    val premiumFeatures: String,

    val screenshotsTitle: String,
    val screenshotsDescription: String,

    val unlimitedHistoryTitle: String,
    val unlimitedHistoryDescription: String,

    val pdfExportTitle: String,
    val pdfExportDescription: String,

    val favoritesTitle: String,
    val favoritesDescription: String,

    val cloudSyncTitle: String,
    val cloudSyncDescription: String,

    val premiumActive: String,
    val allFeaturesUnlocked: String,

    val comparisonTitle: String,
    val screenshots: String,
    val history: String,
    val pdfExport: String,
    val favorites: String,
    val cloudSync: String,
    val unlimited: String,

    val pdfBrandingSettings: String,
    val languageSettings: String,
    val restorePurchase: String,
    val subscriptionNotice: String
)

object PremiumLanguage {

    fun get(language: String): PremiumStrings {
        return if (LanguageManager.isTurkish(language)) {
            turkish
        } else {
            english
        }
    }

    private val turkish = PremiumStrings(
        back = "Geri",
        activeDescription = "Premium üyeliğiniz aktif.",
        inactiveDescription = "Daha detaylı analizlerin ve Premium araçların kilidini açın.",

        monthly = "Aylık",
        monthlyPeriod = "/ ay",
        chooseMonthly = "Aylık Planı Seç",

        yearly = "Yıllık",
        yearlyPeriod = "/ yıl",
        chooseYearly = "Yıllık Planı Seç",
        mostPopular = "EN POPÜLER",
        yearlySaving = "Yıllık planla daha fazla tasarruf edin",

        premiumFeatures = "Premium Özellikler",

        screenshotsTitle = "20 Ekran Görüntüsü",
        screenshotsDescription = "Daha uzun konuşmaları analiz edin.",

        unlimitedHistoryTitle = "Sınırsız Geçmiş",
        unlimitedHistoryDescription = "Önceki raporlarınızın tamamını saklayın.",

        pdfExportTitle = "PDF Dışa Aktarma",
        pdfExportDescription = "Profesyonel raporlar oluşturun.",

        favoritesTitle = "Favoriler",
        favoritesDescription = "Önemli analizleri kaydedin.",

        cloudSyncTitle = "Bulut Senkronizasyonu",
        cloudSyncDescription = "Raporlarınıza diğer cihazlardan erişin.",

        premiumActive = "✓ Premium Aktif",
        allFeaturesUnlocked = "Tüm Premium özelliklerin kilidi açıldı.",

        comparisonTitle = "Ücretsiz ve Premium",
        screenshots = "Ekran Görüntüsü",
        history = "Geçmiş",
        pdfExport = "PDF Dışa Aktarma",
        favorites = "Favoriler",
        cloudSync = "Bulut Senkronizasyonu",
        unlimited = "Sınırsız",

        pdfBrandingSettings = "📄 PDF Marka Ayarları",
        languageSettings = "🌐 Dil Ayarları",
        restorePurchase = "Satın Almayı Geri Yükle",
        subscriptionNotice =
            "Abonelikler Google Play üzerinden iptal edilmediği sürece otomatik olarak yenilenir."
    )

    private val english = PremiumStrings(
        back = "Back",
        activeDescription = "Your Premium membership is active.",
        inactiveDescription = "Unlock deeper analysis and Premium tools.",

        monthly = "Monthly",
        monthlyPeriod = "/ month",
        chooseMonthly = "Choose Monthly",

        yearly = "Yearly",
        yearlyPeriod = "/ year",
        chooseYearly = "Choose Yearly",
        mostPopular = "MOST POPULAR",
        yearlySaving = "Save more with the yearly plan",

        premiumFeatures = "Premium Features",

        screenshotsTitle = "20 Screenshots",
        screenshotsDescription = "Analyze longer conversations.",

        unlimitedHistoryTitle = "Unlimited History",
        unlimitedHistoryDescription = "Keep all previous reports.",

        pdfExportTitle = "PDF Export",
        pdfExportDescription = "Create professional reports.",

        favoritesTitle = "Favorites",
        favoritesDescription = "Save important analyses.",

        cloudSyncTitle = "Cloud Sync",
        cloudSyncDescription = "Access reports on other devices.",

        premiumActive = "✓ Premium Active",
        allFeaturesUnlocked = "All Premium features are unlocked.",

        comparisonTitle = "Free vs Premium",
        screenshots = "Screenshots",
        history = "History",
        pdfExport = "PDF Export",
        favorites = "Favorites",
        cloudSync = "Cloud Sync",
        unlimited = "Unlimited",

        pdfBrandingSettings = "📄 PDF Branding Settings",
        languageSettings = "🌐 Language Settings",
        restorePurchase = "Restore Purchase",
        subscriptionNotice =
            "Subscriptions renew automatically unless cancelled through Google Play."
    )
}