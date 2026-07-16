package com.toxiclens.app.ui

import android.net.Uri
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun PdfBrandingScreen(
    initialLogoUri: String,
    initialCompanyName: String,
    initialPhone: String,
    initialEmail: String,
    initialWebsite: String,
    initialAddress: String,
    onSave: (
        logoUri: String,
        companyName: String,
        phone: String,
        email: String,
        website: String,
        address: String
    ) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var logoUri by remember { mutableStateOf(initialLogoUri) }
    var companyName by remember { mutableStateOf(initialCompanyName) }
    var phone by remember { mutableStateOf(initialPhone) }
    var email by remember { mutableStateOf(initialEmail) }
    var website by remember { mutableStateOf(initialWebsite) }
    var address by remember { mutableStateOf(initialAddress) }

    val logoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            logoUri = it.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7FB))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("← Back")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "📄 PDF Branding",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Add your logo and company information to exported PDF reports.",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(22.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = "Company Logo",
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (logoUri.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(logoUri),
                        contentDescription = "Company Logo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                Button(
                    onClick = {
                        logoPicker.launch(
                            arrayOf("image/png", "image/jpeg", "image/webp")
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (logoUri.isBlank()) {
                            "Choose Logo"
                        } else {
                            "Change Logo"
                        }
                    )
                }

                if (logoUri.isNotBlank()) {
                    TextButton(
                        onClick = {
                            logoUri = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Remove Logo",
                            color = Color(0xFFFF5C7A)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        BrandingTextField(
            value = companyName,
            onValueChange = { companyName = it },
            label = "Company / User Name"
        )

        BrandingTextField(
            value = phone,
            onValueChange = { phone = it },
            label = "Phone"
        )

        BrandingTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email"
        )

        BrandingTextField(
            value = website,
            onValueChange = { website = it },
            label = "Website"
        )

        BrandingTextField(
            value = address,
            onValueChange = { address = it },
            label = "Address",
            singleLine = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                onSave(
                    logoUri,
                    companyName.trim(),
                    phone.trim(),
                    email.trim(),
                    website.trim(),
                    address.trim()
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save PDF Branding")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun BrandingTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
        shape = RoundedCornerShape(16.dp)
    )
}