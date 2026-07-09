package com.toxiclens.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toxiclens.app.data.HistoryStore
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onItemClick: (String, String) -> Unit
) {
    val context = LocalContext.current
    val historyStore = remember { HistoryStore(context) }
    val scope = rememberCoroutineScope()

    var historyItems by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        historyItems = historyStore.getHistory()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7FB))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("← Back")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "📜 Analysis History",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${historyItems.size} saved analyses",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (historyItems.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "No analysis yet.",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Your completed analyses will appear here.",
                        color = Color.Gray
                    )
                }
            }
        } else {
            historyItems.forEachIndexed { index, item ->

                val type = extractHistoryValue(item, "TYPE").ifBlank { "❤️ Relationship" }
                val date = extractHistoryValue(item, "DATE").ifBlank { "-" }
                val result = extractHistoryValue(item, "RESULT").ifBlank { item }

                HistoryItemCard(
                    index = index + 1,
                    conversationType = type,
                    date = date,
                    result = result,
                    onClick = {
                        onItemClick(result, type)
                    },
                    onDelete = {
                        scope.launch {
                            historyStore.deleteAnalysis(item)
                            historyItems = historyStore.getHistory()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun HistoryItemCard(
    index: Int,
    conversationType: String,
    date: String,
    result: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val score = extractSection(result, "RELATIONSHIP_SCORE").ifBlank { "0" }
    val toxicity = extractSection(result, "TOXICITY_LEVEL").ifBlank { "Belirsiz" }
    val summary = extractSection(result, "SUMMARY").ifBlank { "Özet yok." }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "Analysis #$index",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = conversationType,
                color = Color(0xFF6F50B5),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "🕒 $date",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "❤️ Score: $score / 100",
                color = Color(0xFF151525)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "☣ Toxicity: $toxicity",
                color = Color(0xFF151525)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = summary,
                color = Color(0xFF666A7A),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = {
                    showDeleteDialog = true
                }
            ) {
                Text(
                    text = "🗑 Delete",
                    color = Color(0xFFFF5C7A)
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {
                Text("Delete Analysis")
            },
            text = {
                Text("Are you sure you want to delete this analysis?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

fun extractHistoryValue(
    text: String,
    key: String
): String {
    val startTag = "$key::"
    val startIndex = text.indexOf(startTag)

    if (startIndex == -1) return ""

    val contentStart = startIndex + startTag.length

    val nextIndex = text.indexOf("###", contentStart)

    return if (nextIndex == -1) {
        text.substring(contentStart).trim()
    } else {
        text.substring(contentStart, nextIndex).trim()
    }
}