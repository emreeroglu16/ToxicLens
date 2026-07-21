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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.toxiclens.app.strings.AnalyzeLanguage
import com.toxiclens.app.strings.AnalyzeStrings

@Composable
fun AnalyzeScreen(
    appLanguage: String,
    isPremiumUser: Boolean,
    onBack: () -> Unit,
    onImagesSelected: (List<Uri>) -> Unit
) {
    val strings = remember(appLanguage) {
        AnalyzeLanguage.get(appLanguage)
    }

    val screenshotLimit = if (isPremiumUser) {
        20
    } else {
        2
    }

    var selectedImageUris by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        selectedImageUris = uris
            .distinct()
            .take(screenshotLimit)
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
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 22.dp,
                    vertical = 16.dp
                )
        ) {
            TextButton(
                onClick = onBack,
                contentPadding = PaddingValues(
                    horizontal = 0.dp,
                    vertical = 4.dp
                )
            ) {
                Text(
                    text = strings.back,
                    color = Color(0xFFC9CCE8),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = strings.title,
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = strings.description,
                color = Color(0xFFC9CCE8),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Button(
                onClick = {
                    imagePickerLauncher.launch("image/*")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7A3CFF)
                )
            ) {
                Text(
                    text = strings.addScreenshots,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            SelectionSummaryCard(
                selectedCount = selectedImageUris.size,
                screenshotLimit = screenshotLimit,
                isPremiumUser = isPremiumUser,
                strings = strings
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            if (selectedImageUris.isEmpty()) {
                EmptyScreenshotCard(
                    strings = strings,
                    screenshotLimit = screenshotLimit
                )
            } else {
                selectedImageUris.forEachIndexed { index, uri ->
                    ScreenshotPreviewCard(
                        index = index + 1,
                        uri = uri,
                        strings = strings,
                        onRemove = {
                            selectedImageUris =
                                selectedImageUris.filterIndexed { itemIndex, _ ->
                                    itemIndex != index
                                }
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Button(
                onClick = {
                    onImagesSelected(selectedImageUris)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                enabled = selectedImageUris.isNotEmpty(),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE56BFF),
                    contentColor = Color(0xFF151525),
                    disabledContainerColor = Color(0xFF303655),
                    disabledContentColor = Color(0xFF8E92A9)
                )
            ) {
                Text(
                    text = strings.continueButton,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            PremiumCard(
                strings = strings
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )
        }
    }
}

@Composable
fun SelectionSummaryCard(
    selectedCount: Int,
    screenshotLimit: Int,
    isPremiumUser: Boolean,
    strings: AnalyzeStrings
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF151A35)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = strings.selected,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "$selectedCount / $screenshotLimit ${strings.screenshots} · ${
                    if (isPremiumUser) {
                        strings.premiumPlan
                    } else {
                        strings.freePlan
                    }
                }",
                color = Color(0xFFC9CCE8),
                style = MaterialTheme.typography.bodyMedium
            )

            if (
                !isPremiumUser &&
                selectedCount >= screenshotLimit
            ) {
                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Surface(
                    color = Color(0xFF292F50),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = strings.freeLimitReached,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 10.dp
                        ),
                        color = Color(0xFFE9CBFF),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyScreenshotCard(
    strings: AnalyzeStrings,
    screenshotLimit: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF151A35)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = Color(0xFF292F50),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = "▣",
                    modifier = Modifier.padding(
                        horizontal = 18.dp,
                        vertical = 12.dp
                    ),
                    color = Color(0xFFE56BFF),
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = strings.noScreenshotsTitle,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = if (screenshotLimit == 2) {
                    strings.noScreenshotsDescription
                } else {
                    "${strings.description} ($screenshotLimit)"
                },
                color = Color(0xFFC9CCE8),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ScreenshotPreviewCard(
    index: Int,
    uri: Uri,
    strings: AnalyzeStrings,
    onRemove: () -> Unit
) {
    val context = LocalContext.current

    val bitmap = remember(uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(
                context.contentResolver,
                uri
            )

            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(
                context.contentResolver,
                uri
            )
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF151A35)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = index.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(
                        color = Color(0xFF7A3CFF),
                        shape = RoundedCornerShape(50.dp)
                    )
                    .padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription =
                    "${strings.imageContentDescription} $index",
                modifier = Modifier
                    .width(88.dp)
                    .height(108.dp)
                    .clip(
                        RoundedCornerShape(14.dp)
                    ),
                contentScale = ContentScale.Crop
            )

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${strings.screenshot} $index",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = strings.ready,
                    color = Color(0xFF37D67A),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            TextButton(
                onClick = onRemove
            ) {
                Text(
                    text = strings.remove,
                    color = Color(0xFFFF5C7A),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun PremiumCard(
    strings: AnalyzeStrings
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF11162D)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = strings.premiumTitle,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = strings.premiumDescription,
                color = Color(0xFFC9CCE8),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}