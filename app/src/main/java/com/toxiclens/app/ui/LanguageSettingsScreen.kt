package com.toxiclens.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun LanguageSettingsScreen(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7FB))
            .padding(20.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("← Back")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "🌐 Language",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Select the language used in analyses and PDF reports.",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        LanguageOptionCard(
            title = "🇹🇷 Türkçe",
            isSelected = selectedLanguage == "tr",
            onClick = {
                onLanguageSelected("tr")
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LanguageOptionCard(
            title = "🇬🇧 English",
            isSelected = selectedLanguage == "en",
            onClick = {
                onLanguageSelected("en")
            }
        )
    }
}

@Composable
private fun LanguageOptionCard(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                Color(0xFFE9D8FD)
            } else {
                Color.White
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = if (isSelected) "✓" else "",
                color = Color(0xFF6F50B5),
                fontWeight = FontWeight.Bold
            )
        }
    }
}