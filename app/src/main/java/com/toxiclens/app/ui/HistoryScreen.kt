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
import com.toxiclens.app.strings.HistoryLanguage
import com.toxiclens.app.strings.HistoryStrings
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen(
    appLanguage: String,
    isPremiumUser: Boolean,
    onUpgradeClick: () -> Unit,
    onBack: () -> Unit,
    onItemClick: (String, String) -> Unit
) {
    val context = LocalContext.current
    val historyStore = remember { HistoryStore(context) }
    val scope = rememberCoroutineScope()

    val strings = remember(appLanguage) {
        HistoryLanguage.get(appLanguage)
    }

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
        extractHistoryValue(
            text = item,
            key = "FAVORITE"
        ).equals(
            other = "true",
            ignoreCase = true
        )
    }

    val visibleItems = if (isPremiumUser) {
        sortedItems
    } else {
        sortedItems.take(5)
    }

    val filteredItems = visibleItems.filter { item ->
        val type = extractHistoryValue(
            text = item,
            key = "TYPE"
        )

        val date = extractHistoryValue(
            text = item,
            key = "DATE"
        )

        val result = extractHistoryValue(
            text = item,
            key = "RESULT"
        ).ifBlank {
            item
        }

        val score = extractSection(
            result,
            "RELATIONSHIP_SCORE"
        )

        val toxicity = extractSection(
            result,
            "TOXICITY_LEVEL"
        )

        val summary = extractSection(
            result,
            "SUMMARY"
        )

        searchText.isBlank() ||
                type.contains(
                    other = searchText,
                    ignoreCase = true
                ) ||
                date.contains(
                    other = searchText,
                    ignoreCase = true
                ) ||
                score.contains(
                    other = searchText,
                    ignoreCase = true
                ) ||
                toxicity.contains(
                    other = searchText,
                    ignoreCase = true
                ) ||
                summary.contains(
                    other = searchText,
                    ignoreCase = true
                )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7FB))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        TextButton(
            onClick = onBack
        ) {
            Text(
                text = "← ${strings.back}",
                color = Color(0xFF6F50B5)
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = strings.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF151525)
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = if (isPremiumUser) {
                strings.savedAnalyses(
                    historyItems.size
                )
            } else {
                strings.shownAnalyses(
                    visibleItems.size,
                    historyItems.size
                )
            },
            color = Color(0xFF666A7A)
        )

        if (!isPremiumUser && historyItems.size > 5) {
            Spacer(
                modifier = Modifier.height(14.dp)
            )

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
                        text = strings.freeLimitTitle,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF151525)
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = strings.freeLimitDescription,
                        color = Color(0xFF665A32)
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Button(
                        onClick = onUpgradeClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = strings.upgradeToPremium
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        OutlinedTextField(
            value = searchText,
            onValueChange = { newValue ->
                searchText = newValue
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = strings.searchLabel
                )
            },
            placeholder = {
                Text(
                    text = strings.searchPlaceholder
                )
            },
            leadingIcon = {
                Text(
                    text = "🔍"
                )
            },
            trailingIcon = {
                if (searchText.isNotBlank()) {
                    TextButton(
                        onClick = {
                            searchText = ""
                        }
                    ) {
                        Text(
                            text = "✕"
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(18.dp)
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        when {
            visibleItems.isEmpty() -> {
                EmptyHistoryCard(
                    title = strings.noAnalysisTitle,
                    description = strings.noAnalysisDescription
                )
            }

            filteredItems.isEmpty() -> {
                EmptyHistoryCard(
                    title = strings.noMatchingTitle,
                    description = strings.noMatchingDescription
                )
            }

            else -> {
                filteredItems.forEachIndexed { index, item ->
                    val type = extractHistoryValue(
                        text = item,
                        key = "TYPE"
                    ).ifBlank {
                        "❤️ Relationship"
                    }

                    val date = extractHistoryValue(
                        text = item,
                        key = "DATE"
                    ).ifBlank {
                        "-"
                    }

                    val result = extractHistoryValue(
                        text = item,
                        key = "RESULT"
                    ).ifBlank {
                        item
                    }

                    val isFavorite = extractHistoryValue(
                        text = item,
                        key = "FAVORITE"
                    ).equals(
                        other = "true",
                        ignoreCase = true
                    )

                    HistoryItemCard(
                        index = index + 1,
                        conversationType = type,
                        date = date,
                        result = result,
                        isFavorite = isFavorite,
                        strings = strings,
                        onClick = {
                            onItemClick(
                                result,
                                type
                            )
                        },
                        onFavoriteClick = {
                            if (isPremiumUser) {
                                scope.launch {
                                    historyStore.toggleFavorite(item)
                                    historyItems =
                                        historyStore.getHistory()
                                }
                            } else {
                                showPremiumDialog = true
                            }
                        },
                        onDelete = {
                            scope.launch {
                                historyStore.deleteAnalysis(item)
                                historyItems =
                                    historyStore.getHistory()
                            }
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }

    if (showPremiumDialog) {
        AlertDialog(
            onDismissRequest = {
                showPremiumDialog = false
            },
            title = {
                Text(
                    text = strings.premiumFeatureTitle
                )
            },
            text = {
                Text(
                    text = strings.favoritesPremiumDescription
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
                        text = strings.upgrade
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 24.dp,
                    vertical = 28.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📭",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF151525)
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = description,
                color = Color(0xFF666A7A),
                style = MaterialTheme.typography.bodyMedium
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
    strings: HistoryStrings,
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
    ).ifBlank {
        "0"
    }

    val toxicity = extractSection(
        result,
        "TOXICITY_LEVEL"
    ).ifBlank {
        strings.unknown
    }

    val summary = extractSection(
        result,
        "SUMMARY"
    ).ifBlank {
        strings.noSummary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
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
                    text = strings.analysisNumber(index),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF151525),
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onFavoriteClick
                ) {
                    Text(
                        text = if (isFavorite) {
                            "★"
                        } else {
                            "☆"
                        },
                        color = Color(0xFFFFB300),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }

            Text(
                text = conversationType,
                color = Color(0xFF6F50B5),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = "🕒 $date",
                color = Color(0xFF777B8A),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = Color(0xFFF0EBFF),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = "⭐ ${strings.score}: $score / 100",
                        color = Color(0xFF4E3687),
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        )
                    )
                }

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Surface(
                    color = Color(0xFFFFEEF1),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = "☣ ${strings.toxicity}: $toxicity",
                        color = Color(0xFF9B344B),
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        )
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = summary,
                color = Color(0xFF666A7A),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            TextButton(
                onClick = {
                    showDeleteDialog = true
                }
            ) {
                Text(
                    text = strings.delete,
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
                Text(
                    text = strings.deleteAnalysisTitle
                )
            },
            text = {
                Text(
                    text = strings.deleteAnalysisDescription
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text(
                        text = strings.delete,
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
                    Text(
                        text = strings.cancel
                    )
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
    val nextIndex = text.indexOf(
        "###",
        contentStart
    )

    return if (nextIndex == -1) {
        text.substring(contentStart).trim()
    } else {
        text.substring(
            contentStart,
            nextIndex
        ).trim()
    }
}