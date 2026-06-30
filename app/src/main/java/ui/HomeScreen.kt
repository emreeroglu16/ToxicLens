package com.toxiclens.app.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onAnalyzeClick: () -> Unit
) {
    Box(
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            Text(
                text = "Welcome 👋",
                color = Color(0xFFC9CCE8),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            Text(
                text = "Let's understand conversations.",
                color = Color(0xFF8F94BD),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(Modifier.height(24.dp))

            AppLogo()

            Spacer(Modifier.height(14.dp))

            Text(
                text = "Read\nBetween",
                color = Color.White,
                fontSize = 42.sp,
                lineHeight = 42.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Understand what words really mean.",
                color = Color(0xFFC9CCE8),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(30.dp))

            FeatureCard(
                onClick = onAnalyzeClick,
                icon = "▣",
                title = "Analyze Screenshot",
                badge = "AI Powered",
                description = "Analyze WhatsApp, Instagram, SMS or Messenger screenshots.",
                accentColor = Color(0xFFE56BFF)
            )

            FeatureCard(
                icon = "☰",
                title = "Paste Conversation",
                badge = "Fast",
                description = "Paste copied messages and let AI analyze the communication.",
                accentColor = Color(0xFF2F8CFF)
            )

            FeatureCard(
                icon = "◷",
                title = "Previous Analyses",
                badge = "Saved",
                description = "View your saved analysis reports and history.",
                accentColor = Color(0xFF37D67A)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                SmallCard(
                    modifier = Modifier.weight(1f),
                    icon = "◎",
                    title = "Language",
                    description = "English",
                    accentColor = Color(0xFF9B5CFF)
                )

                SmallCard(
                    modifier = Modifier.weight(1f),
                    icon = "♙",
                    title = "Sign in",
                    description = "Save analyses",
                    accentColor = Color(0xFF2F8CFF)
                )
            }

            Spacer(Modifier.height(16.dp))

            PrivacyCard()

            Spacer(Modifier.height(30.dp))

            Text(
                text = "Read Between v1.0",
                color = Color(0xFF8B90B8),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun AppLogo() {
    Box(
        modifier = Modifier
            .size(88.dp)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF8E5CFF),
                        Color(0xFFE56BFF),
                        Color(0xFF2F8CFF)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "♡",
            color = Color.White,
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FeatureCard(
    onClick: () -> Unit = {},
    icon: String,
    title: String,
    badge: String,
    description: String,
    accentColor: Color
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        label = "cardScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            },
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF151A35)
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        accentColor.copy(alpha = 0.20f),
                        RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    color = accentColor,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(18.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(6.dp))

                BadgePill(
                    text = badge,
                    color = accentColor
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = description,
                    color = Color(0xFFC9CCE8),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "›",
                color = Color.White,
                fontSize = 36.sp
            )
        }
    }
}

@Composable
fun BadgePill(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .background(
                color.copy(alpha = 0.16f),
                RoundedCornerShape(50.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
fun SmallCard(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    description: String,
    accentColor: Color
) {
    Card(
        modifier = modifier.height(138.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.30f)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF151A35)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        accentColor.copy(alpha = 0.18f),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    color = accentColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = description,
                    color = accentColor,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun PrivacyCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFF9B5CFF).copy(alpha = 0.30f)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF11162D)
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color(0xFF9B5CFF).copy(alpha = 0.16f),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⌾",
                    color = Color(0xFFE56BFF),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = "Your privacy is our priority",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Your conversations are never stored or shared without your permission.",
                    color = Color(0xFFC9CCE8),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}