package com.toxiclens.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.toxiclens.app.strings.PremiumLanguage
import com.toxiclens.app.strings.PremiumStrings

@Composable
fun PremiumScreen(
    appLanguage: String,
    isPremium: Boolean,
    onMonthlyClick: () -> Unit,
    onYearlyClick: () -> Unit,
    onRestoreClick: () -> Unit,
    onPdfBrandingClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onBack: () -> Unit
) {
    val strings = remember(appLanguage) {
        PremiumLanguage.get(appLanguage)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF050716),
                        Color(0xFF101638),
                        Color(0xFF050612)
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(22.dp)
    ) {
        TextButton(onClick = onBack) {
            Text(
                text = "← ${strings.back}",
                color = Color(0xFFC9CCE8)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "⭐ Read Between Premium",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isPremium) {
                strings.activeDescription
            } else {
                strings.inactiveDescription
            },
            color = Color(0xFFC9CCE8),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isPremium) {
            PremiumActiveCard(strings)
        } else {
            PlanCard(
                title = strings.monthly,
                price = "₺99,99",
                period = strings.monthlyPeriod,
                buttonText = strings.chooseMonthly,
                buttonColor = Color(0xFF7A3CFF),
                onClick = onMonthlyClick
            )

            Spacer(modifier = Modifier.height(14.dp))

            PlanCard(
                title = strings.yearly,
                price = "₺799,99",
                period = strings.yearlyPeriod,
                buttonText = strings.chooseYearly,
                buttonColor = Color(0xFFE56BFF),
                badge = strings.mostPopular,
                savingText = strings.yearlySaving,
                onClick = onYearlyClick
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = strings.premiumFeatures,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(14.dp))

        PremiumFeatureCard(
            icon = "🖼️",
            title = strings.screenshotsTitle,
            description = strings.screenshotsDescription
        )

        PremiumFeatureCard(
            icon = "📜",
            title = strings.unlimitedHistoryTitle,
            description = strings.unlimitedHistoryDescription
        )

        PremiumFeatureCard(
            icon = "📄",
            title = strings.pdfExportTitle,
            description = strings.pdfExportDescription
        )

        PremiumFeatureCard(
            icon = "⭐",
            title = strings.favoritesTitle,
            description = strings.favoritesDescription
        )

        PremiumFeatureCard(
            icon = "☁️",
            title = strings.cloudSyncTitle,
            description = strings.cloudSyncDescription
        )

        Spacer(modifier = Modifier.height(12.dp))

        ComparisonCard(strings)

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = onPdfBrandingClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF151A35)
            )
        ) {
            Text(strings.pdfBrandingSettings)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onLanguageClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF151A35)
            )
        ) {
            Text(strings.languageSettings)
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onRestoreClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = strings.restorePurchase,
                color = Color(0xFFC9CCE8)
            )
        }

        Text(
            text = strings.subscriptionNotice,
            color = Color(0xFF8B90B8),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun PlanCard(
    title: String,
    price: String,
    period: String,
    buttonText: String,
    buttonColor: Color,
    badge: String? = null,
    savingText: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF151A35)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            if (badge != null) {
                Surface(
                    color = Color(0xFFFFC857),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = badge,
                        color = Color(0xFF251B00),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = price,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = period,
                    color = Color(0xFFC9CCE8),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (savingText != null) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = savingText,
                    color = Color(0xFFFFC857),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor
                )
            ) {
                Text(buttonText)
            }
        }
    }
}

@Composable
private fun PremiumActiveCard(
    strings: PremiumStrings
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF183D32)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = strings.premiumActive,
                color = Color(0xFF71E6A7),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = strings.allFeaturesUnlocked,
                color = Color.White
            )
        }
    }
}

@Composable
private fun PremiumFeatureCard(
    icon: String,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF151A35)
        )
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    color = Color(0xFFC9CCE8)
                )
            }
        }
    }
}

@Composable
private fun ComparisonCard(
    strings: PremiumStrings
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF11162D)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = strings.comparisonTitle,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(14.dp))

            ComparisonRow(strings.screenshots, "2", "20")
            ComparisonRow(strings.history, "5", strings.unlimited)
            ComparisonRow(strings.pdfExport, "—", "✓")
            ComparisonRow(strings.favorites, "—", "✓")
            ComparisonRow(strings.cloudSync, "—", "✓")
        }
    }
}

@Composable
private fun ComparisonRow(
    feature: String,
    freeValue: String,
    premiumValue: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = feature,
            color = Color(0xFFC9CCE8),
            modifier = Modifier.weight(1.4f)
        )

        Text(
            text = freeValue,
            color = Color(0xFF8B90B8),
            modifier = Modifier.weight(0.6f),
            textAlign = TextAlign.Center
        )

        Text(
            text = premiumValue,
            color = Color(0xFFFFC857),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.8f),
            textAlign = TextAlign.End
        )
    }
}