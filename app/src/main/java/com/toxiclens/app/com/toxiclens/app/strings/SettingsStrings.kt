package com.toxiclens.app.strings

data class SettingsStrings(
    val back: String,
    val title: String,
    val description: String,
    val preferences: String,
    val language: String,
    val turkish: String,
    val english: String,
    val pdfBranding: String,
    val pdfBrandingDescription: String,
    val account: String,
    val premiumActive: String,
    val premiumInactive: String,
    val supportAndLegal: String,
    val privacyPolicy: String,
    val privacyPolicyDescription: String,
    val contactUs: String,
    val contactUsDescription: String,
    val rateApp: String,
    val rateAppDescription: String,
    val about: String,
    val aboutDescription: String
)

object SettingsLanguage {

    fun get(language: String): SettingsStrings {
        return if (LanguageManager.isTurkish(language)) {
            turkish
        } else {
            english
        }
    }

    private val turkish = SettingsStrings(
        back = "Geri",
        title = "⚙️ Ayarlar",
        description = "Uygulama tercihlerini ve hesap özelliklerini yönetin.",
        preferences = "Tercihler",
        language = "Dil",
        turkish = "Türkçe",
        english = "İngilizce",
        pdfBranding = "PDF Marka Ayarları",
        pdfBrandingDescription = "Logo ve firma bilgilerini düzenleyin.",
        account = "Hesap",
        premiumActive = "Premium üyeliğiniz aktif.",
        premiumInactive = "Premium özelliklerin kilidini açın.",
        supportAndLegal = "Destek ve Yasal",
        privacyPolicy = "Gizlilik Politikası",
        privacyPolicyDescription = "Verilerinizin nasıl işlendiğini görüntüleyin.",
        contactUs = "Bize Ulaşın",
        contactUsDescription = "Destek veya geri bildirim gönderin.",
        rateApp = "Uygulamayı Değerlendir",
        rateAppDescription = "Read Between'i Google Play'de değerlendirin.",
        about = "Hakkında",
        aboutDescription = "Sürüm ve uygulama bilgileri."
    )

    private val english = SettingsStrings(
        back = "Back",
        title = "⚙️ Settings",
        description = "Manage your application preferences and account features.",
        preferences = "Preferences",
        language = "Language",
        turkish = "Turkish",
        english = "English",
        pdfBranding = "PDF Branding",
        pdfBrandingDescription = "Edit your logo and company information.",
        account = "Account",
        premiumActive = "Your Premium membership is active.",
        premiumInactive = "Unlock Premium features.",
        supportAndLegal = "Support & Legal",
        privacyPolicy = "Privacy Policy",
        privacyPolicyDescription = "See how your data is handled.",
        contactUs = "Contact Us",
        contactUsDescription = "Send support requests or feedback.",
        rateApp = "Rate App",
        rateAppDescription = "Rate Read Between on Google Play.",
        about = "About",
        aboutDescription = "Version and application information."
    )
}