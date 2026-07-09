package com.toxiclens.app.ui

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toxiclens.app.ai.GeminiService
import kotlinx.coroutines.launch

@Composable
fun ConversationTypeScreen(
    imageUris: List<Uri>,
    onAnalysisComplete: (String, String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val geminiService = remember { GeminiService() }

    val types = listOf(
        "❤️ Relationship",
        "👥 Friend",
        "👨‍👩‍👧 Family",
        "💼 Boss",
        "👔 Coworker",
        "🛒 Customer",
        "📱 Other"
    )

    var selected by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(false) }

    fun uriToBitmap(uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7FB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            TextButton(onClick = onBack) {
                Text("← Back")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Who is this conversation with?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Select one option before starting the analysis.",
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            types.forEach { type ->
                val isSelected = selected == type

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable {
                            selected = type
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFFE9D8FD) else Color.White
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = type,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    scope.launch {
                        isAnalyzing = true

                        val bitmaps = imageUris.map { uri ->
                            uriToBitmap(uri)
                        }

                        val result = geminiService.analyze(
                            bitmaps = bitmaps,
                            conversationType = selected
                        )

                        isAnalyzing = false

                        onAnalysisComplete(result, selected)
                    }
                },
                enabled = selected.isNotEmpty() && !isAnalyzing,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Analyze Conversation")
            }
        }

        if (isAnalyzing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.62f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF151A35)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFE56BFF),
                            modifier = Modifier.size(42.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Analiz ediliyor...",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Gemini seçilen kategoriye göre inceliyor.",
                            color = Color(0xFFC9CCE8),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}