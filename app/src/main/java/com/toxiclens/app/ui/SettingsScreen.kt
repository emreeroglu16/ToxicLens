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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
    val isTurkish = appLanguage == "tr"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7FB))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        TextButton(onClick = onBack) {
            Text(
                text = if (isTurkish) "← Geri" else "← Back",
                color = Color(0xFF6F50B5)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (isTurkish) {
                "⚙️ Ayarlar"
            } else {
                "⚙️ Settings"
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF151525)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isTurkish) {
                "Uygulama tercihlerini ve hesap özelliklerini yönetin."
            } else {
                "Manage your application preferences and account features."
            },
            color = Color(0xFF666A7A)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSectionTitle(
            text = if (isTurkish) "Tercihler" else "Preferences"
        )

        SettingsItemCard(
            icon = "🌐",
            title = if (isTurkish) "Dil" else "Language",
            description = if (isTurkish) {
                if (appLanguage == "tr") "Türkçe" else "İngilizce"
            } else {
                if (appLanguage == "tr") "Turkish" else "English"
            },
            onClick = onLanguageClick
        )

        SettingsItemCard(
            icon = "📄",
            title = if (isTurkish) {
                "PDF Marka Ayarları"
            } else {
                "PDF Branding"
            },
            description = if (isTurkish) {
                "Logo ve firma bilgilerini düzenleyin."
            } else {
                "Edit your logo and company information."
            },
            onClick = onPdfBrandingClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingsSectionTitle(
            text = if (isTurkish) "Hesap" else "Account"
        )

        SettingsItemCard(
            icon = "⭐",
            title = "Read Between Premium",
            description = if (isPremium) {
                if (isTurkish) {
                    "Premium üyeliğiniz aktif."
                } else {
                    "Your Premium membership is active."
                }
            } else {
                if (isTurkish) {
                    "Premium özelliklerin kilidini açın."
                } else {
                    "Unlock Premium features."
                }
            },
            onClick = onPremiumClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingsSectionTitle(
            text = if (isTurkish) "Destek ve Yasal" else "Support & Legal"
        )

        SettingsItemCard(
            icon = "📜",
            title = if (isTurkish) {
                "Gizlilik Politikası"
            } else {
                "Privacy Policy"
            },
            description = if (isTurkish) {
                "Verilerinizin nasıl işlendiğini görüntüleyin."
            } else {
                "See how your data is handled."
            },
            onClick = onPrivacyClick
        )

        SettingsItemCard(
            icon = "📧",
            title = if (isTurkish) {
                "Bize Ulaşın"
            } else {
                "Contact Us"
            },
            description = if (isTurkish) {
                "Destek veya geri bildirim gönderin."
            } else {
                "Send support requests or feedback."
            },
            onClick = onContactClick
        )

        SettingsItemCard(
            icon = "⭐",
            title = if (isTurkish) {
                "Uygulamayı Değerlendir"
            } else {
                "Rate App"
            },
            description = if (isTurkish) {
                "Read Between'i Google Play'de değerlendirin."
            } else {
                "Rate Read Between on Google Play."
            },
            onClick = onRateAppClick
        )

        SettingsItemCard(
            icon = "ℹ️",
            title = if (isTurkish) "Hakkında" else "About",
            description = if (isTurkish) {
                "Sürüm ve uygulama bilgileri."
            } else {
                "Version and application information."
            },
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