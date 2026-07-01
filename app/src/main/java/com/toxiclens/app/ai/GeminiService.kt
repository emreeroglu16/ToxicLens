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

    suspend fun analyze(bitmap: Bitmap): String {
        return try {
            val prompt = """
Sen Read Between uygulamasının iletişim analiz motorusun.

Bu ekran görüntüsündeki konuşmayı analiz et.

Cevabı Türkçe ver.

Çok kısa, net ve kullanıcı dostu yaz.

Aşağıdaki formatı aynen kullan:

İlişki Skoru: 0-100 arası puan

Duygusal Ton: En fazla 1 cümle

Gizli Niyet: En fazla 1 cümle

Manipülasyon Riski: Düşük / Orta / Yüksek

Kırmızı Bayraklar: En fazla 1 cümle

Özet: En fazla 2 cümle

Tavsiye: En fazla 1 cümle

Kurallar:
- Uzun paragraf yazma.
- Gereksiz detay verme.
- Kesin psikolojik teşhis koyma.
- Sadece ekrandaki konuşmaya göre yorum yap.
- Eğer konuşma net okunamıyorsa bunu açıkça belirt.
""".trimIndent()

            val response = model.generateContent(
                content {
                    image(bitmap)
                    text(prompt)
                }
            )

            response.text ?: "Analiz sonucu alınamadı."

        } catch (e: Exception) {
            e.localizedMessage ?: "Bilinmeyen hata oluştu."
        }
    }
}