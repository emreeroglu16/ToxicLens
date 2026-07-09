package com.toxiclens.app.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ResultScreen(
    result: String,
    conversationType: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = SnackbarHostState()
    val scope = rememberCoroutineScope()

    val score = extractSection(result, "RELATIONSHIP_SCORE").ifBlank { "0" }
    val toxicity = extractSection(result, "TOXICITY_LEVEL").ifBlank { "Belirsiz" }
    val emotion = extractSection(result, "EMOTIONAL_TONE").ifBlank { "Belirlenemedi." }
    val intent = extractSection(result, "HIDDEN_INTENT").ifBlank { "Belirgin bir gizli niyet tespit edilemedi." }
    val greenFlags = extractSection(result, "GREEN_FLAGS").ifBlank { "Belirgin olumlu işaret yok." }
    val redFlags = extractSection(result, "RED_FLAGS").ifBlank { "Belirgin kırmızı bayrak yok." }
    val summary = extractSection(result, "SUMMARY").ifBlank { "Özet çıkarılamadı." }
    val suggestedReply = extractSection(result, "SUGGESTED_REPLY").ifBlank { "Cevap önerisi oluşturulamadı." }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF7F7FB)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF7F7FB))
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            TextButton(onClick = onBack) {
                Text("← Back", color = Color(0xFF6F50B5))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "🧠 Analysis Complete",
                color = Color(0xFF151525),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Conversation analyzed successfully.",
                color = Color(0xFF666A7A),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(22.dp))

            ScoreCard(
                scoreText = score,
                conversationType = conversationType
            )

            Spacer(modifier = Modifier.height(14.dp))

            ToxicityCard(toxicity)

            ResultCard("😊 Emotional Tone", emotion, Color.White)
            ResultCard("🧠 Hidden Intent", cleanIntent(intent), Color.White)
            ResultCard("💚 Green Flags", formatFlags(greenFlags, "✅"), Color(0xFFEFFBF4))
            ResultCard("🚩 Red Flags", formatFlags(redFlags, "⚠️"), Color(0xFFFFF1F3))
            ResultCard("📝 Summary", summary, Color.White)

            SuggestedReplyCard(
                reply = suggestedReply,
                onCopy = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(
                        ClipData.newPlainText("Suggested Reply", suggestedReply)
                    )
                    scope.launch {
                        snackbarHostState.showSnackbar("Reply copied")
                    }
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "ℹ️ This AI analysis is based only on the screenshots you provided. It should be treated as a helpful suggestion, not a factual judgment.",
                color = Color(0xFF777A88),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6F50B5)
                )
            ) {
                Text("Back to Analysis")
            }
        }
    }
}

@Composable
fun ScoreCard(
    scoreText: String,
    conversationType: String
) {
    val score = scoreText.filter { it.isDigit() }.toIntOrNull()?.coerceIn(0, 100) ?: 0
    val progress = score / 100f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF151A35)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${conversationType.trim()} Score",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "$score",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.displayLarge
            )

            Text(
                text = "/100",
                color = Color(0xFFC9CCE8),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = conversationType,
                color = Color(0xFFE56BFF),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(50.dp)),
                color = Color(0xFFE56BFF),
                trackColor = Color(0xFF303655)
            )
        }
    }
}

@Composable
fun ToxicityCard(toxicity: String) {
    val clean = toxicity.trim()

    val badgeColor = when {
        clean.contains("yüksek", ignoreCase = true) -> Color(0xFFFFD6DC)
        clean.contains("orta", ignoreCase = true) -> Color(0xFFFFF0C2)
        clean.contains("düşük", ignoreCase = true) -> Color(0xFFDFF8EA)
        else -> Color(0xFFEDEDF5)
    }

    val badgeText = when {
        clean.contains("yüksek", ignoreCase = true) -> "🔴 YÜKSEK"
        clean.contains("orta", ignoreCase = true) -> "🟡 ORTA"
        clean.contains("düşük", ignoreCase = true) -> "🟢 DÜŞÜK"
        else -> clean.uppercase()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "☣ Toxicity Level",
                color = Color(0xFF151525),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = badgeColor,
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = badgeText,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color(0xFF202030),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ResultCard(
    title: String,
    content: String,
    containerColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                color = Color(0xFF151525),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = content,
                color = Color(0xFF303040),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun SuggestedReplyCard(
    reply: String,
    onCopy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEFEFFF)
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "💬 Suggested Reply",
                color = Color(0xFF151525),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = reply,
                color = Color(0xFF303040),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onCopy,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6F50B5)
                )
            ) {
                Text("📋 Copy Reply")
            }
        }
    }
}

fun extractSection(
    text: String,
    sectionName: String
): String {
    val sectionStart = "$sectionName:"
    val startIndex = text.indexOf(sectionStart)

    if (startIndex == -1) return ""

    val contentStart = startIndex + sectionStart.length

    val nextSections = listOf(
        "RELATIONSHIP_SCORE:",
        "TOXICITY_LEVEL:",
        "EMOTIONAL_TONE:",
        "HIDDEN_INTENT:",
        "GREEN_FLAGS:",
        "RED_FLAGS:",
        "SUMMARY:",
        "SUGGESTED_REPLY:"
    )

    val nextIndex = nextSections
        .filter { it != sectionStart }
        .map { text.indexOf(it, contentStart) }
        .filter { it != -1 }
        .minOrNull() ?: text.length

    return text.substring(contentStart, nextIndex).trim()
}

fun formatFlags(text: String, icon: String): String {
    return text
        .lines()
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .joinToString("\n") { line ->
            val cleanLine = line
                .removePrefix("*")
                .removePrefix("-")
                .removePrefix("•")
                .trim()

            if (
                cleanLine.contains("yok", ignoreCase = true) ||
                cleanLine.contains("belirgin", ignoreCase = true).not()
            ) {
                "$icon $cleanLine"
            } else {
                cleanLine
            }
        }
}

fun cleanIntent(text: String): String {
    return if (text.contains("net değil", ignoreCase = true)) {
        "Belirgin bir gizli niyet tespit edilemedi."
    } else {
        text
    }
}