package com.toxiclens.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toxiclens.app.data.HistoryStore
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen(
    isPremiumUser: Boolean,
    onUpgradeClick: () -> Unit,
    onBack: () -> Unit,
    onItemClick: (String, String) -> Unit
) {
    val context = LocalContext.current
    val historyStore = remember { HistoryStore(context) }
    val scope = rememberCoroutineScope()

    var historyItems by remember {
        mutableStateOf<List<String>>(emptyList())
    }

    var searchText by remember {
        mutableStateOf("")
    }

    var showPremiumDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        historyItems = historyStore.getHistory()
    }

    val sortedItems = historyItems.sortedByDescending { item ->
        extractHistoryValue(item, "FAVORITE")
            .equals("true", ignoreCase = true)
    }

    val visibleItems = if (isPremiumUser) {
        sortedItems
    } else {
        sortedItems.take(5)
    }

    val filteredItems = visibleItems.filter { item ->
        val type = extractHistoryValue(item, "TYPE")
        val date = extractHistoryValue(item, "DATE")
        val result = extractHistoryValue(item, "RESULT").ifBlank { item }

        val score = extractSection(result, "RELATIONSHIP_SCORE")
        val toxicity = extractSection(result, "TOXICITY_LEVEL")
        val summary = extractSection(result, "SUMMARY")

        searchText.isBlank() ||
                type.contains(searchText, ignoreCase = true) ||
                date.contains(searchText, ignoreCase = true) ||
                score.contains(searchText, ignoreCase = true) ||
                toxicity.contains(searchText, ignoreCase = true) ||
                summary.contains(searchText, ignoreCase = true)
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
            text = if (isPremiumUser) {
                "${historyItems.size} saved analyses"
            } else {
                "${visibleItems.size} of ${historyItems.size} analyses shown"
            },
            color = Color.Gray
        )

        if (!isPremiumUser && historyItems.size > 5) {
            Spacer(modifier = Modifier.height(14.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF2CC)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "🔒 Free history limit reached",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Free users can view the latest 5 analyses.")

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onUpgradeClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Upgrade to Premium")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Search history")
            },
            placeholder = {
                Text("Family, Boss, date, toxicity...")
            },
            leadingIcon = {
                Text("🔍")
            },
            trailingIcon = {
                if (searchText.isNotBlank()) {
                    TextButton(
                        onClick = {
                            searchText = ""
                        }
                    ) {
                        Text("✕")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(18.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        when {
            visibleItems.isEmpty() -> {
                EmptyHistoryCard(
                    title = "No analysis yet.",
                    description = "Your completed analyses will appear here."
                )
            }

            filteredItems.isEmpty() -> {
                EmptyHistoryCard(
                    title = "No matching analysis.",
                    description = "Try a different search term."
                )
            }

            else -> {
                filteredItems.forEachIndexed { index, item ->
                    val type = extractHistoryValue(item, "TYPE")
                        .ifBlank { "❤️ Relationship" }

                    val date = extractHistoryValue(item, "DATE")
                        .ifBlank { "-" }

                    val result = extractHistoryValue(item, "RESULT")
                        .ifBlank { item }

                    val isFavorite = extractHistoryValue(
                        item,
                        "FAVORITE"
                    ).equals("true", ignoreCase = true)

                    HistoryItemCard(
                        index = index + 1,
                        conversationType = type,
                        date = date,
                        result = result,
                        isFavorite = isFavorite,
                        onClick = {
                            onItemClick(result, type)
                        },
                        onFavoriteClick = {
                            if (isPremiumUser) {
                                scope.launch {
                                    historyStore.toggleFavorite(item)
                                    historyItems = historyStore.getHistory()
                                }
                            } else {
                                showPremiumDialog = true
                            }
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
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showPremiumDialog) {
        AlertDialog(
            onDismissRequest = {
                showPremiumDialog = false
            },
            title = {
                Text("⭐ Premium Feature")
            },
            text = {
                Text("Favorites is available with Read Between Premium.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPremiumDialog = false
                        onUpgradeClick()
                    }
                ) {
                    Text("Upgrade")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPremiumDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EmptyHistoryCard(
    title: String,
    description: String
) {
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
                text = title,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun HistoryItemCard(
    index: Int,
    conversationType: String,
    date: String,
    result: String,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    val score = extractSection(
        result,
        "RELATIONSHIP_SCORE"
    ).ifBlank { "0" }

    val toxicity = extractSection(
        result,
        "TOXICITY_LEVEL"
    ).ifBlank { "Belirsiz" }

    val summary = extractSection(
        result,
        "SUMMARY"
    ).ifBlank { "Özet yok." }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Analysis #$index",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onFavoriteClick
                ) {
                    Text(
                        text = if (isFavorite) "★" else "☆",
                        color = Color(0xFFFFB300),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }

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
                text = "⭐ Score: $score / 100",
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
                    Text(
                        text = "Delete",
                        color = Color(0xFFFF5C7A)
                    )
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

    if (startIndex == -1) {
        return ""
    }

    val contentStart = startIndex + startTag.length
    val nextIndex = text.indexOf("###", contentStart)

    return if (nextIndex == -1) {
        text.substring(contentStart).trim()
    } else {
        text.substring(contentStart, nextIndex).trim()
    }
}