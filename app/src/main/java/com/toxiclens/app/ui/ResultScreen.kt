package com.toxiclens.app.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.toxiclens.app.data.PdfBranding
import com.toxiclens.app.pdf.PdfReportGenerator
import com.toxiclens.app.strings.ResultLanguage
import com.toxiclens.app.strings.ResultStrings
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ResultScreen(
    result: String,
    conversationType: String,
    isPremiumUser: Boolean,
    pdfBranding: PdfBranding,
    appLanguage: String,
    onUpgradeClick: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val strings = remember(appLanguage) {
        ResultLanguage.get(appLanguage)
    }

    var showPremiumDialog by remember {
        mutableStateOf(false)
    }

    val score = extractSection(
        result,
        "RELATIONSHIP_SCORE"
    ).ifBlank {
        "0"
    }

    val toxicity = extractSection(
        result,
        "TOXICITY_LEVEL"
    ).ifBlank {
        strings.unknown
    }

    val emotion = extractSection(
        result,
        "EMOTIONAL_TONE"
    ).ifBlank {
        strings.emotionFallback
    }

    val intent = extractSection(
        result,
        "HIDDEN_INTENT"
    ).ifBlank {
        strings.intentFallback
    }

    val greenFlags = extractSection(
        result,
        "GREEN_FLAGS"
    ).ifBlank {
        strings.greenFlagsFallback
    }

    val redFlags = extractSection(
        result,
        "RED_FLAGS"
    ).ifBlank {
        strings.redFlagsFallback
    }

    val summary = extractSection(
        result,
        "SUMMARY"
    ).ifBlank {
        strings.summaryFallback
    }

    val suggestedReply = extractSection(
        result,
        "SUGGESTED_REPLY"
    ).ifBlank {
        strings.suggestedReplyFallback
    }

    val createPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(
            "application/pdf"
        )
    ) { uri ->
        if (uri != null) {
            val success = PdfReportGenerator.createPdf(
                context = context,
                outputUri = uri,
                result = result,
                conversationType = conversationType,
                branding = pdfBranding,
                appLanguage = appLanguage
            )

            scope.launch {
                snackbarHostState.showSnackbar(
                    message = if (success) {
                        strings.pdfSavedSuccessfully
                    } else {
                        strings.pdfCreationFailed
                    }
                )
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
        containerColor = Color(0xFFF7F7FB)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF7F7FB))
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                )
        ) {
            TextButton(
                onClick = onBack,
                contentPadding = PaddingValues(
                    horizontal = 0.dp,
                    vertical = 4.dp
                )
            ) {
                Text(
                    text = strings.back,
                    color = Color(0xFF6F50B5),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = strings.analysisComplete,
                color = Color(0xFF151525),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = strings.analysisCompleteDescription,
                color = Color(0xFF666A7A),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            ScoreCard(
                scoreText = score,
                conversationType = conversationType,
                scoreLabel = strings.score
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            PdfExportButton(
                isPremiumUser = isPremiumUser,
                strings = strings,
                onClick = {
                    if (isPremiumUser) {
                        val date = SimpleDateFormat(
                            "yyyyMMdd_HHmm",
                            Locale.getDefault()
                        ).format(Date())

                        createPdfLauncher.launch(
                            "Read_Between_Report_$date.pdf"
                        )
                    } else {
                        showPremiumDialog = true
                    }
                }
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            ToxicityCard(
                toxicity = toxicity,
                strings = strings
            )

            ResultCard(
                title = strings.emotionalTone,
                content = emotion,
                containerColor = Color.White
            )

            ResultCard(
                title = strings.hiddenIntent,
                content = cleanIntent(
                    text = intent,
                    fallback = strings.intentFallback
                ),
                containerColor = Color.White
            )

            ResultCard(
                title = strings.greenFlags,
                content = formatFlags(
                    text = greenFlags,
                    icon = "✅"
                ),
                containerColor = Color(0xFFEFFBF4)
            )

            ResultCard(
                title = strings.redFlags,
                content = formatFlags(
                    text = redFlags,
                    icon = "⚠️"
                ),
                containerColor = Color(0xFFFFF1F3)
            )

            ResultCard(
                title = strings.summary,
                content = summary,
                containerColor = Color.White
            )

            SuggestedReplyCard(
                reply = suggestedReply,
                strings = strings,
                onCopy = {
                    val clipboard = context.getSystemService(
                        Context.CLIPBOARD_SERVICE
                    ) as ClipboardManager

                    clipboard.setPrimaryClip(
                        ClipData.newPlainText(
                            strings.suggestedReply,
                            suggestedReply
                        )
                    )

                    scope.launch {
                        snackbarHostState.showSnackbar(
                            strings.replyCopied
                        )
                    }
                }
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            DisclaimerCard(
                text = strings.disclaimer
            )

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6F50B5)
                )
            ) {
                Text(
                    text = strings.backToAnalysis,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )
        }
    }

    if (showPremiumDialog) {
        AlertDialog(
            onDismissRequest = {
                showPremiumDialog = false
            },
            title = {
                Text(
                    text = strings.premiumPdfTitle,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = strings.premiumPdfDescription
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPremiumDialog = false
                        onUpgradeClick()
                    }
                ) {
                    Text(
                        text = strings.upgrade,
                        color = Color(0xFF6F50B5),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPremiumDialog = false
                    }
                ) {
                    Text(
                        text = strings.cancel
                    )
                }
            }
        )
    }
}

@Composable
fun ScoreCard(
    scoreText: String,
    conversationType: String,
    scoreLabel: String
) {
    val score = scoreText
        .filter {
            it.isDigit()
        }
        .toIntOrNull()
        ?.coerceIn(
            minimumValue = 0,
            maximumValue = 100
        )
        ?: 0

    val progress = score / 100f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF151A35)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = Color(0xFF292F50),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = conversationType.trim(),
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 7.dp
                    ),
                    color = Color(0xFFE9CBFF),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = score.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.displayLarge
            )

            Text(
                text = "/100",
                color = Color(0xFFC9CCE8),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = scoreLabel,
                color = Color(0xFFE56BFF),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            LinearProgressIndicator(
                progress = {
                    progress
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(
                        RoundedCornerShape(50.dp)
                    ),
                color = Color(0xFFE56BFF),
                trackColor = Color(0xFF303655)
            )
        }
    }
}

@Composable
fun PdfExportButton(
    isPremiumUser: Boolean,
    strings: ResultStrings,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPremiumUser) {
                Color(0xFF151A35)
            } else {
                Color(0xFF373B54)
            }
        )
    ) {
        Text(
            text = if (isPremiumUser) {
                strings.exportPdf
            } else {
                strings.exportPdfPremium
            },
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ToxicityCard(
    toxicity: String,
    strings: ResultStrings
) {
    val clean = toxicity.trim()

    val toxicityLevel = when {
        isHighToxicity(clean) -> ToxicityLevel.HIGH
        isMediumToxicity(clean) -> ToxicityLevel.MEDIUM
        isLowToxicity(clean) -> ToxicityLevel.LOW
        else -> ToxicityLevel.UNKNOWN
    }

    val badgeColor = when (toxicityLevel) {
        ToxicityLevel.HIGH -> Color(0xFFFFD6DC)
        ToxicityLevel.MEDIUM -> Color(0xFFFFF0C2)
        ToxicityLevel.LOW -> Color(0xFFDFF8EA)
        ToxicityLevel.UNKNOWN -> Color(0xFFEDEDF5)
    }

    val badgeTextColor = when (toxicityLevel) {
        ToxicityLevel.HIGH -> Color(0xFFA12442)
        ToxicityLevel.MEDIUM -> Color(0xFF826100)
        ToxicityLevel.LOW -> Color(0xFF167247)
        ToxicityLevel.UNKNOWN -> Color(0xFF555568)
    }

    val badgeText = when (toxicityLevel) {
        ToxicityLevel.HIGH -> "● ${strings.toxicityHigh}"
        ToxicityLevel.MEDIUM -> "● ${strings.toxicityMedium}"
        ToxicityLevel.LOW -> "● ${strings.toxicityLow}"
        ToxicityLevel.UNKNOWN -> clean.ifBlank {
            strings.unknown
        }.uppercase()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.toxicityLevel,
                color = Color(0xFF151525),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            Surface(
                color = badgeColor,
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = badgeText,
                    modifier = Modifier.padding(
                        horizontal = 15.dp,
                        vertical = 8.dp
                    ),
                    color = badgeTextColor,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
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
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = title,
                color = Color(0xFF151525),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

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
    strings: ResultStrings,
    onCopy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEFEFFF)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = strings.suggestedReply,
                color = Color(0xFF151525),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White.copy(
                    alpha = 0.72f
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = reply,
                    modifier = Modifier.padding(16.dp),
                    color = Color(0xFF303040),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Button(
                onClick = onCopy,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6F50B5)
                )
            ) {
                Text(
                    text = strings.copyReply,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DisclaimerCard(
    text: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFEFEFF5),
        shape = RoundedCornerShape(18.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            color = Color(0xFF686B79),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private enum class ToxicityLevel {
    HIGH,
    MEDIUM,
    LOW,
    UNKNOWN
}

private fun isHighToxicity(
    text: String
): Boolean {
    return text.contains(
        "yüksek",
        ignoreCase = true
    ) || text.contains(
        "high",
        ignoreCase = true
    )
}

private fun isMediumToxicity(
    text: String
): Boolean {
    return text.contains(
        "orta",
        ignoreCase = true
    ) || text.contains(
        "medium",
        ignoreCase = true
    ) || text.contains(
        "moderate",
        ignoreCase = true
    )
}

private fun isLowToxicity(
    text: String
): Boolean {
    return text.contains(
        "düşük",
        ignoreCase = true
    ) || text.contains(
        "low",
        ignoreCase = true
    )
}

fun extractSection(
    text: String,
    sectionName: String
): String {
    val sectionStart = "$sectionName:"
    val startIndex = text.indexOf(sectionStart)

    if (startIndex == -1) {
        return ""
    }

    val contentStart =
        startIndex + sectionStart.length

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
        .filter {
            it != sectionStart
        }
        .map {
            text.indexOf(
                it,
                contentStart
            )
        }
        .filter {
            it != -1
        }
        .minOrNull()
        ?: text.length

    return text.substring(
        contentStart,
        nextIndex
    ).trim()
}

fun formatFlags(
    text: String,
    icon: String
): String {
    return text
        .lines()
        .map {
            it.trim()
        }
        .filter {
            it.isNotBlank()
        }
        .joinToString("\n") { line ->
            val cleanLine = line
                .removePrefix("*")
                .removePrefix("-")
                .removePrefix("•")
                .removePrefix("✅")
                .removePrefix("⚠️")
                .trim()

            "$icon $cleanLine"
        }
}

fun cleanIntent(
    text: String,
    fallback: String
): String {
    val unclearExpressions = listOf(
        "net değil",
        "belirsiz",
        "not clear",
        "unclear"
    )

    return if (
        unclearExpressions.any { expression ->
            text.contains(
                expression,
                ignoreCase = true
            )
        }
    ) {
        fallback
    } else {
        text
    }
}