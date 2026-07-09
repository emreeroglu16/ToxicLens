package com.toxiclens.app.ai

import android.graphics.Bitmap
import com.google.firebase.ai.FirebaseAI
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content

class GeminiService {

    private val model = FirebaseAI.getInstance(
        backend = GenerativeBackend.googleAI()
    ).generativeModel(
        modelName = "gemini-2.5-flash"
    )

    suspend fun analyze(
        bitmaps: List<Bitmap>,
        conversationType: String
    ): String {
        return try {
            val prompt = """
Sen Read Between uygulamasının iletişim analiz motorusun.

Seçilen konuşma türü:
$conversationType

Bu görseller aynı konuşmanın devam eden ekran görüntüleridir.
Görselleri sırayla değerlendir.
Analizi seçilen konuşma türüne göre yap.
Cevabı Türkçe ver.
Çok kısa, net ve kullanıcı dostu yaz.

Aşağıdaki formatı aynen kullan:

RELATIONSHIP_SCORE:
0-100 arası tek bir puan yaz.

TOXICITY_LEVEL:
Düşük / Orta / Yüksek

EMOTIONAL_TONE:
Konuşmanın baskın duygusal tonunu en fazla 1 cümleyle yaz.

HIDDEN_INTENT:
Varsa gizli niyeti en fazla 1 cümleyle açıkla. Yeterli kanıt yoksa "Belirgin bir gizli niyet tespit edilemedi." yaz.

GREEN_FLAGS:
Olumlu işaretleri kısa maddeler halinde yaz. Yoksa "Belirgin olumlu işaret yok" yaz.

RED_FLAGS:
Uyarı işaretlerini kısa maddeler halinde yaz. Yoksa "Belirgin kırmızı bayrak yok" yaz.

SUMMARY:
Konuşmanın genel özetini en fazla 2 cümleyle yaz.

SUGGESTED_REPLY:
Kullanıcının gönderebileceği doğal ve kısa bir cevap öner.

Kurallar:
- Uzun paragraf yazma.
- Kesin psikolojik teşhis koyma.
- Kişilere hakaret etme.
- Sadece ekrandaki konuşmalara göre yorum yap.
- Görsellerin sırasını konuşmanın akışı olarak kabul et.
- Eğer konuşma net okunamıyorsa bunu açıkça belirt.
""".trimIndent()

            val response = model.generateContent(
                content {
                    bitmaps.forEach { bitmap ->
                        image(bitmap)
                    }
                    text(prompt)
                }
            )

            response.text ?: "Analiz sonucu alınamadı."

        } catch (e: Exception) {
            e.localizedMessage ?: "Bilinmeyen hata oluştu."
        }
    }
}