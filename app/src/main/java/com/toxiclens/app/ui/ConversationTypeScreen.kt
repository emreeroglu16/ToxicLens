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
import com.toxiclens.app.strings.ConversationTypeLanguage
import com.toxiclens.app.strings.ConversationTypeStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private data class ConversationTypeOption(
    val value: String,
    val emoji: String,
    val label: (ConversationTypeStrings) -> String
)

@Composable
fun ConversationTypeScreen(
    imageUris: List<Uri>,
    appLanguage: String,
    onAnalysisComplete: (String, String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val geminiService = remember { GeminiService() }

    val strings = remember(appLanguage) {
        ConversationTypeLanguage.get(appLanguage)
    }

    val types = remember {
        listOf(
            ConversationTypeOption(
                value = "❤️ Relationship",
                emoji = "❤️",
                label = { it.relationship }
            ),
            ConversationTypeOption(
                value = "👥 Friend",
                emoji = "👥",
                label = { it.friend }
            ),
            ConversationTypeOption(
                value = "👨‍👩‍👧 Family",
                emoji = "👨‍👩‍👧",
                label = { it.family }
            ),
            ConversationTypeOption(
                value = "💼 Boss",
                emoji = "💼",
                label = { it.boss }
            ),
            ConversationTypeOption(
                value = "👔 Coworker",
                emoji = "👔",
                label = { it.coworker }
            ),
            ConversationTypeOption(
                value = "🛒 Customer",
                emoji = "🛒",
                label = { it.customer }
            ),
            ConversationTypeOption(
                value = "📱 Other",
                emoji = "📱",
                label = { it.other }
            )
        )
    }

    var selectedType by remember {
        mutableStateOf<ConversationTypeOption?>(null)
    }

    var isAnalyzing by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    suspend fun uriToBitmap(uri: Uri): Bitmap {
        return withContext(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(
                    context.contentResolver,
                    uri
                )

                ImageDecoder.decodeBitmap(source)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(
                    context.contentResolver,
                    uri
                )
            }
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
            TextButton(
                onClick = onBack,
                enabled = !isAnalyzing
            ) {
                Text("← ${strings.back}")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = strings.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = strings.description,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            types.forEach { type ->
                val isSelected = selectedType?.value == type.value

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable(enabled = !isAnalyzing) {
                            selectedType = type
                            errorMessage = null
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            Color(0xFFE9D8FD)
                        } else {
                            Color.White
                        }
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
                            text = "${type.emoji} ${type.label(strings)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val selected = selectedType ?: return@Button

                    scope.launch {
                        isAnalyzing = true
                        errorMessage = null

                        try {
                            val bitmaps = imageUris.map { uri ->
                                uriToBitmap(uri)
                            }

                            val result = geminiService.analyze(
                                bitmaps = bitmaps,
                                conversationType = selected.value,
                                appLanguage = appLanguage
                            )

                            onAnalysisComplete(
                                result,
                                selected.value
                            )
                        } catch (exception: Exception) {
                            errorMessage = exception.localizedMessage
                                ?: strings.analysisError
                        } finally {
                            isAnalyzing = false
                        }
                    }
                },
                enabled = selectedType != null &&
                        !isAnalyzing &&
                        imageUris.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(strings.analyzeConversation)
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
                            text = strings.analyzing,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = strings.geminiAnalyzing,
                            color = Color(0xFFC9CCE8),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}