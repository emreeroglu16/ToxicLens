package com.toxiclens.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toxiclens.app.strings.SettingsLanguage

@Composable
fun SettingsScreen(
    appLanguage: String,
    isPremium: Boolean,
    onLanguageClick: () -> Unit,
    onPdfBrandingClick: () -> Unit,
    onPremiumClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onContactClick: () -> Unit,
    onRateAppClick: () -> Unit,
    onAboutClick: () -> Unit,
    onBack: () -> Unit
) {
    val strings = remember(appLanguage) {
        SettingsLanguage.get(appLanguage)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7FB))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        TextButton(onClick = onBack) {
            Text(
                text = "← ${strings.back}",
                color = Color(0xFF6F50B5)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = strings.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF151525)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = strings.description,
            color = Color(0xFF666A7A)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSectionTitle(
            text = strings.preferences
        )

        SettingsItemCard(
            icon = "🌐",
            title = strings.language,
            description = if (appLanguage == "tr") {
                strings.turkish
            } else {
                strings.english
            },
            onClick = onLanguageClick
        )

        SettingsItemCard(
            icon = "📄",
            title = strings.pdfBranding,
            description = strings.pdfBrandingDescription,
            onClick = onPdfBrandingClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingsSectionTitle(
            text = strings.account
        )

        SettingsItemCard(
            icon = "⭐",
            title = "Read Between Premium",
            description = if (isPremium) {
                strings.premiumActive
            } else {
                strings.premiumInactive
            },
            onClick = onPremiumClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingsSectionTitle(
            text = strings.supportAndLegal
        )

        SettingsItemCard(
            icon = "📜",
            title = strings.privacyPolicy,
            description = strings.privacyPolicyDescription,
            onClick = onPrivacyClick
        )

        SettingsItemCard(
            icon = "📧",
            title = strings.contactUs,
            description = strings.contactUsDescription,
            onClick = onContactClick
        )

        SettingsItemCard(
            icon = "⭐",
            title = strings.rateApp,
            description = strings.rateAppDescription,
            onClick = onRateAppClick
        )

        SettingsItemCard(
            icon = "ℹ️",
            title = strings.about,
            description = strings.aboutDescription,
            onClick = onAboutClick
        )

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun SettingsSectionTitle(
    text: String
) {
    Text(
        text = text,
        color = Color(0xFF6F50B5),
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(
            start = 4.dp,
            bottom = 10.dp
        )
    )
}

@Composable
private fun SettingsItemCard(
    icon: String,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = Color(0xFF151525),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    color = Color(0xFF666A7A),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "›",
                color = Color(0xFF8B90B8),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}