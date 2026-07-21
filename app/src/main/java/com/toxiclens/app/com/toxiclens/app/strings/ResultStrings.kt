package com.toxiclens.app.strings

data class ResultStrings(
    val back: String,
    val analysisComplete: String,
    val analysisCompleteDescription: String,

    val score: String,

    val exportPdf: String,
    val exportPdfPremium: String,
    val pdfSavedSuccessfully: String,
    val pdfCreationFailed: String,

    val toxicityLevel: String,
    val toxicityHigh: String,
    val toxicityMedium: String,
    val toxicityLow: String,
    val unknown: String,

    val emotionalTone: String,
    val hiddenIntent: String,
    val greenFlags: String,
    val redFlags: String,
    val summary: String,

    val suggestedReply: String,
    val copyReply: String,
    val replyCopied: String,

    val disclaimer: String,
    val backToAnalysis: String,

    val premiumPdfTitle: String,
    val premiumPdfDescription: String,
    val upgrade: String,
    val cancel: String,

    val emotionFallback: String,
    val intentFallback: String,
    val greenFlagsFallback: String,
    val redFlagsFallback: String,
    val summaryFallback: String,
    val suggestedReplyFallback: String
)

object ResultLanguage {

    fun get(language: String): ResultStrings {
        return when (language.lowercase()) {
            "tr", "turkish", "türkçe" -> turkish
            else -> english
        }
    }

    private val english = ResultStrings(
        back = "← Back",
        analysisComplete = "🧠 Analysis Complete",
        analysisCompleteDescription =
            "Conversation analyzed successfully.",

        score = "Score",

        exportPdf = "📄 Export PDF",
        exportPdfPremium = "🔒 Export PDF — Premium",
        pdfSavedSuccessfully =
            "PDF report saved successfully.",
        pdfCreationFailed =
            "PDF report could not be created.",

        toxicityLevel = "☣ Toxicity Level",
        toxicityHigh = "HIGH",
        toxicityMedium = "MEDIUM",
        toxicityLow = "LOW",
        unknown = "Unknown",

        emotionalTone = "😊 Emotional Tone",
        hiddenIntent = "🧠 Hidden Intent",
        greenFlags = "💚 Green Flags",
        redFlags = "🚩 Red Flags",
        summary = "📝 Summary",

        suggestedReply = "💬 Suggested Reply",
        copyReply = "📋 Copy Reply",
        replyCopied = "Reply copied",

        disclaimer =
            "ℹ️ This AI analysis is based only on the screenshots you provided. It should be treated as a helpful suggestion, not a factual judgment.",

        backToAnalysis = "Back to Analysis",

        premiumPdfTitle = "📄 Premium PDF Export",
        premiumPdfDescription =
            "PDF export and corporate branding are available with Read Between Premium.",
        upgrade = "Upgrade",
        cancel = "Cancel",

        emotionFallback =
            "The emotional tone could not be determined.",
        intentFallback =
            "No clear hidden intent was detected.",
        greenFlagsFallback =
            "No clear positive signs were detected.",
        redFlagsFallback =
            "No clear red flags were detected.",
        summaryFallback =
            "A summary could not be generated.",
        suggestedReplyFallback =
            "A suggested reply could not be generated."
    )

    private val turkish = ResultStrings(
        back = "← Geri",
        analysisComplete = "🧠 Analiz Tamamlandı",
        analysisCompleteDescription =
            "Konuşma başarıyla analiz edildi.",

        score = "Skor",

        exportPdf = "📄 PDF Olarak Dışa Aktar",
        exportPdfPremium = "🔒 PDF Dışa Aktarma — Premium",
        pdfSavedSuccessfully =
            "PDF raporu başarıyla kaydedildi.",
        pdfCreationFailed =
            "PDF raporu oluşturulamadı.",

        toxicityLevel = "☣ Toksisite Seviyesi",
        toxicityHigh = "YÜKSEK",
        toxicityMedium = "ORTA",
        toxicityLow = "DÜŞÜK",
        unknown = "Belirsiz",

        emotionalTone = "😊 Duygusal Ton",
        hiddenIntent = "🧠 Gizli Niyet",
        greenFlags = "💚 Olumlu İşaretler",
        redFlags = "🚩 Kırmızı Bayraklar",
        summary = "📝 Özet",

        suggestedReply = "💬 Önerilen Cevap",
        copyReply = "📋 Cevabı Kopyala",
        replyCopied = "Cevap kopyalandı",

        disclaimer =
            "ℹ️ Bu yapay zekâ analizi yalnızca sağladığınız ekran görüntülerine dayanır. Kesin bir yargı değil, yardımcı bir öneri olarak değerlendirilmelidir.",

        backToAnalysis = "Analize Geri Dön",

        premiumPdfTitle = "📄 Premium PDF Dışa Aktarma",
        premiumPdfDescription =
            "PDF dışa aktarma ve kurumsal marka özellikleri Read Between Premium ile kullanılabilir.",
        upgrade = "Yükselt",
        cancel = "İptal",

        emotionFallback =
            "Duygusal ton belirlenemedi.",
        intentFallback =
            "Belirgin bir gizli niyet tespit edilemedi.",
        greenFlagsFallback =
            "Belirgin olumlu bir işaret tespit edilemedi.",
        redFlagsFallback =
            "Belirgin bir kırmızı bayrak tespit edilemedi.",
        summaryFallback =
            "Özet oluşturulamadı.",
        suggestedReplyFallback =
            "Cevap önerisi oluşturulamadı."
    )
}