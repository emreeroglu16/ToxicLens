package com.toxiclens.app.ui

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AnalyzeScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val selectedBitmap = selectedImageUri?.let { uri ->
        remember(uri) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        }
    }

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

            Spacer(modifier = Modifier.height(24.dp))

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

            Spacer(modifier = Modifier.height(28.dp))

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
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (selectedBitmap == null) {
                        Text(
                            text = "▣",
                            color = Color(0xFFE56BFF),
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
                            text = "Choose an image from your gallery.",
                            color = Color(0xFFC9CCE8)
                        )
                    } else {
                        Image(
                            bitmap = selectedBitmap.asImageBitmap(),
                            contentDescription = "Selected screenshot",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(360.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            imagePickerLauncher.launch("image/*")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Select Screenshot")
                    }

                    if (selectedBitmap != null) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                // AI analiz işlemi sonraki adımda eklenecek
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Analyze")
                        }
                    }
                }
            }
        }
    }
}