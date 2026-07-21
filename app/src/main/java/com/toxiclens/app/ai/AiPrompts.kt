package com.toxiclens.app.ai

object AiPrompts {

    fun get(
        language: String,
        conversationType: String
    ): String {
        return if (isTurkish(language)) {
            turkishPrompt(
                conversationType = localizedConversationType(
                    conversationType = conversationType,
                    language = "tr"
                )
            )
        } else {
            englishPrompt(
                conversationType = localizedConversationType(
                    conversationType = conversationType,
                    language = "en"
                )
            )
        }
    }

    fun errorMessage(language: String): String {
        return if (isTurkish(language)) {
            "Bilinmeyen bir hata oluştu."
        } else {
            "An unknown error occurred."
        }
    }

    fun emptyResultMessage(language: String): String {
        return if (isTurkish(language)) {
            "Analiz sonucu alınamadı."
        } else {
            "The analysis result could not be retrieved."
        }
    }

    private fun isTurkish(language: String): Boolean {
        val normalizedLanguage = language
            .trim()
            .lowercase()

        return normalizedLanguage == "tr" ||
                normalizedLanguage.startsWith("tr-") ||
                normalizedLanguage.contains("türk") ||
                normalizedLanguage.contains("turkish")
    }

    private fun localizedConversationType(
        conversationType: String,
        language: String
    ): String {
        val normalizedType = conversationType.lowercase()

        return if (language == "tr") {
            when {
                normalizedType.contains("relationship") -> "❤️ Romantik İlişki"
                normalizedType.contains("friend") -> "👥 Arkadaş"
                normalizedType.contains("family") -> "👨‍👩‍👧 Aile"
                normalizedType.contains("boss") -> "💼 Yönetici"
                normalizedType.contains("coworker") -> "👔 İş Arkadaşı"
                normalizedType.contains("customer") -> "🛒 Müşteri"
                else -> "📱 Diğer"
            }
        } else {
            when {
                normalizedType.contains("relationship") -> "❤️ Relationship"
                normalizedType.contains("friend") -> "👥 Friend"
                normalizedType.contains("family") -> "👨‍👩‍👧 Family"
                normalizedType.contains("boss") -> "💼 Boss"
                normalizedType.contains("coworker") -> "👔 Coworker"
                normalizedType.contains("customer") -> "🛒 Customer"
                else -> "📱 Other"
            }
        }
    }

    private fun turkishPrompt(conversationType: String): String {
        return """
Sen Read Between uygulamasının iletişim analiz motorusun.

Seçilen konuşma türü:
$conversationType

Bu görseller aynı konuşmanın devam eden ekran görüntüleridir.
Görselleri yüklendikleri sıraya göre değerlendir.
Analizi seçilen konuşma türüne göre yap.
Cevabı yalnızca Türkçe ver.
Çok kısa, net ve kullanıcı dostu yaz.

Aşağıdaki formatı aynen kullan:

RELATIONSHIP_SCORE:
0-100 arasında yalnızca tek bir sayı yaz.

TOXICITY_LEVEL:
Yalnızca şu seçeneklerden birini yaz:
Düşük
Orta
Yüksek

EMOTIONAL_TONE:
Konuşmanın baskın duygusal tonunu en fazla 1 cümleyle yaz.

HIDDEN_INTENT:
Varsa gizli niyeti en fazla 1 cümleyle açıkla.
Yeterli kanıt yoksa:
Belirgin bir gizli niyet tespit edilemedi.
yaz.

GREEN_FLAGS:
Olumlu işaretleri kısa maddeler halinde yaz.
Olumlu işaret yoksa:
Belirgin olumlu işaret yok.
yaz.

RED_FLAGS:
Uyarı işaretlerini kısa maddeler halinde yaz.
Uyarı işareti yoksa:
Belirgin kırmızı bayrak yok.
yaz.

SUMMARY:
Konuşmanın genel değerlendirmesini en fazla 2 cümleyle yaz.

SUGGESTED_REPLY:
Kullanıcının gönderebileceği doğal, sakin ve kısa bir cevap öner.

Kurallar:
- Başlıkları kesinlikle değiştirme.
- Belirtilen formatın dışına çıkma.
- Uzun paragraf yazma.
- Kesin psikolojik teşhis koyma.
- Kişilere hakaret etme.
- Görsellerde bulunmayan bilgileri uydurma.
- Yalnızca ekrandaki konuşmaya göre yorum yap.
- Görsellerin sırasını konuşmanın akışı olarak kabul et.
- Konuşma net okunamıyorsa bunu açıkça belirt.
- Tehlike, tehdit veya şiddet ifadesi varsa bunu kırmızı bayraklarda açıkça belirt.
""".trimIndent()
    }

    private fun englishPrompt(conversationType: String): String {
        return """
You are the communication analysis engine of the Read Between application.

Selected conversation type:
$conversationType

These images are consecutive screenshots from the same conversation.
Review the images in the order in which they were uploaded.
Analyze the conversation according to the selected conversation type.
Respond only in English.
Keep the response very short, clear and user-friendly.

Use exactly the following format:

RELATIONSHIP_SCORE:
Write only one number between 0 and 100.

TOXICITY_LEVEL:
Write only one of the following:
Low
Moderate
High

EMOTIONAL_TONE:
Describe the dominant emotional tone of the conversation in no more than 1 sentence.

HIDDEN_INTENT:
Explain any possible hidden intention in no more than 1 sentence.
When there is insufficient evidence, write:
No clear hidden intention was detected.

GREEN_FLAGS:
List positive signs using short bullet points.
When there are no clear positive signs, write:
No clear positive signs.

RED_FLAGS:
List warning signs using short bullet points.
When there are no clear warning signs, write:
No clear red flags.

SUMMARY:
Summarize the overall conversation in no more than 2 sentences.

SUGGESTED_REPLY:
Suggest a natural, calm and short reply that the user could send.

Rules:
- Do not change the section headings.
- Do not deviate from the required format.
- Do not write long paragraphs.
- Do not make definitive psychological diagnoses.
- Do not insult anyone.
- Do not invent information that is not visible in the images.
- Base the analysis only on the displayed conversation.
- Treat the image order as the conversation flow.
- Clearly state when the conversation cannot be read properly.
- Clearly mention threats, violence or dangerous statements under red flags.
""".trimIndent()
    }
}