package com.toxiclens.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AnalyzeScreen(
    onBack: () -> Unit
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
            .padding(22.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TextButton(onClick = onBack) {
                Text("← Back", color = Color(0xFFC9CCE8))
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Analyze Screenshot",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Select a screenshot from WhatsApp, Instagram, SMS or Messenger to analyze the conversation.",
                color = Color(0xFFC9CCE8),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF151A35)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📷",
                        style = MaterialTheme.typography.displayMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "No screenshot selected yet",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Gallery selection will be added in the next step.",
                        color = Color(0xFFC9CCE8)
                    )
                }
            }
        }
    }
}