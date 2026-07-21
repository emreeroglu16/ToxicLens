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
        conversationType: String,
        appLanguage: String
    ): String {
        return try {
            val prompt = AiPrompts.get(
                language = appLanguage,
                conversationType = conversationType
            )

            val response = model.generateContent(
                content {
                    bitmaps.forEach { bitmap ->
                        image(bitmap)
                    }

                    text(prompt)
                }
            )

            response.text
                ?.trim()
                ?.takeIf { it.isNotEmpty() }
                ?: AiPrompts.emptyResultMessage(appLanguage)

        } catch (exception: Exception) {
            exception.localizedMessage
                ?.takeIf { it.isNotBlank() }
                ?: AiPrompts.errorMessage(appLanguage)
        }
    }
}