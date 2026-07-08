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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
    onBack: () -> Unit,
    onImagesSelected: (List<Uri>) -> Unit
) {
    val isPremiumUser = false
    val screenshotLimit = if (isPremiumUser) 20 else 2

    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        selectedImageUris = uris.take(screenshotLimit)
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(22.dp)
        ) {
            TextButton(onClick = onBack) {
                Text("← Back", color = Color(0xFFC9CCE8))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Conversation Screenshots",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Select screenshots from the same conversation in the correct order.",
                color = Color(0xFFC9CCE8),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    imagePickerLauncher.launch("image/*")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Add Screenshots")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF151A35)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Selected",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${selectedImageUris.size} / $screenshotLimit screenshots - ${if (isPremiumUser) "Premium Plan" else "Free Plan"}",
                        color = Color(0xFFC9CCE8)
                    )

                    if (!isPremiumUser && selectedImageUris.size >= screenshotLimit) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Free limit reached. Premium will allow up to 20 screenshots.",
                            color = Color(0xFFE56BFF),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedImageUris.isEmpty()) {
                EmptyScreenshotCard()
            } else {
                selectedImageUris.forEachIndexed { index, uri ->
                    ScreenshotPreviewCard(
                        index = index + 1,
                        uri = uri,
                        onRemove = {
                            selectedImageUris = selectedImageUris.filterIndexed { i, _ ->
                                i != index
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    onImagesSelected(selectedImageUris)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedImageUris.isNotEmpty()
            ) {
                Text("Continue")
            }

            Spacer(modifier = Modifier.height(20.dp))

            PremiumCard()

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun EmptyScreenshotCard() {
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
                text = "▣",
                color = Color(0xFFE56BFF),
                style = MaterialTheme.typography.displayMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No screenshots selected yet",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose up to 2 screenshots for free.",
                color = Color(0xFFC9CCE8)
            )
        }
    }
}

@Composable
fun ScreenshotPreviewCard(
    index: Int,
    uri: Uri,
    onRemove: () -> Unit
) {
    val context = LocalContext.current

    val bitmap = remember(uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF151A35)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$index",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(Color(0xFF7A3CFF), RoundedCornerShape(50.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Screenshot $index",
                modifier = Modifier
                    .width(90.dp)
                    .height(110.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Screenshot $index",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "✓ Ready",
                    color = Color(0xFF37D67A),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            TextButton(onClick = onRemove) {
                Text("Remove", color = Color(0xFFFF5C7A))
            }
        }
    }
}

@Composable
fun PremiumCard() {
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
                text = "⭐ Premium",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Analyze up to 20 screenshots, get detailed reports, history and PDF export.",
                color = Color(0xFFC9CCE8),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}