package com.toxiclens.app

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.toxiclens.app.billing.BillingManager
import com.toxiclens.app.data.AppLanguageStore
import com.toxiclens.app.data.HistoryStore
import com.toxiclens.app.data.PdfBranding
import com.toxiclens.app.data.PdfBrandingStore
import com.toxiclens.app.ui.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val context = LocalContext.current
                val historyStore = remember { HistoryStore(context) }
                val pdfBrandingStore = remember { PdfBrandingStore(context) }
                val languageStore = remember { AppLanguageStore(context) }
                val scope = rememberCoroutineScope()

                var currentScreen by remember {
                    mutableStateOf("home")
                }

                var analysisResult by remember {
                    mutableStateOf("")
                }

                var selectedImageUris by remember {
                    mutableStateOf<List<Uri>>(emptyList())
                }

                var selectedConversationType by remember {
                    mutableStateOf("❤️ Relationship")
                }

                var isPremium by remember {
                    mutableStateOf(false)
                }

                var appLanguage by remember {
                    mutableStateOf("tr")
                }

                var pdfBranding by remember {
                    mutableStateOf(PdfBranding())
                }

                // Geçici Premium testi.
                // Google Play yayını öncesinde false yapılacak.
                val forcePremiumForTesting = true
                val effectivePremium =
                    forcePremiumForTesting || isPremium

                LaunchedEffect(Unit) {
                    pdfBranding = pdfBrandingStore.getBranding()
                    appLanguage = languageStore.getLanguage()
                }

                val billingManager = remember {
                    BillingManager(
                        context = applicationContext,
                        onPremiumChanged = { premium ->
                            isPremium = premium
                        },
                        onError = { message ->
                            runOnUiThread {
                                Toast.makeText(
                                    this@MainActivity,
                                    message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    )
                }

                DisposableEffect(Unit) {
                    billingManager.startConnection()

                    onDispose {
                        billingManager.endConnection()
                    }
                }

                when (currentScreen) {

                    "home" -> {
                        HomeScreen(
                            appLanguage = appLanguage,
                            onAnalyzeClick = {
                                currentScreen = "analyze"
                            },
                            onHistoryClick = {
                                currentScreen = "history"
                            },
                            onPremiumClick = {
                                currentScreen = "premium"
                            },
                            onSettingsClick = {
                                currentScreen = "settings"
                            }
                        )
                    }

                    "settings" -> {
                        SettingsScreen(
                            appLanguage = appLanguage,
                            isPremium = effectivePremium,
                            onLanguageClick = {
                                currentScreen = "language"
                            },
                            onPdfBrandingClick = {
                                currentScreen = "pdfBranding"
                            },
                            onPremiumClick = {
                                currentScreen = "premium"
                            },
                            onPrivacyClick = {},
                            onContactClick = {},
                            onRateAppClick = {},
                            onAboutClick = {},
                            onBack = {
                                currentScreen = "home"
                            }
                        )
                    }

                    "analyze" -> {
                        AnalyzeScreen(
                            appLanguage = appLanguage,
                            isPremiumUser = effectivePremium,
                            onBack = {
                                currentScreen = "home"
                            },
                            onImagesSelected = { uris ->
                                selectedImageUris = uris
                                currentScreen = "conversationType"
                            }
                        )
                    }

                    "conversationType" -> {
                        ConversationTypeScreen(
                            imageUris = selectedImageUris,
                            appLanguage = appLanguage,
                            onBack = {
                                currentScreen = "analyze"
                            },
                            onAnalysisComplete = { result, type ->
                                analysisResult = result
                                selectedConversationType = type

                                scope.launch {
                                    historyStore.saveAnalysis(
                                        result = result,
                                        conversationType = type
                                    )
                                }

                                currentScreen = "result"
                            }
                        )
                    }

                    "result" -> {
                        ResultScreen(
                            result = analysisResult,
                            conversationType = selectedConversationType,
                            isPremiumUser = effectivePremium,
                            pdfBranding = pdfBranding,
                            appLanguage = appLanguage,
                            onUpgradeClick = {
                                currentScreen = "premium"
                            },
                            onBack = {
                                currentScreen = "conversationType"
                            }
                        )
                    }

                    "history" -> {
                        HistoryScreen(
                            appLanguage = appLanguage,
                            isPremiumUser = effectivePremium,
                            onUpgradeClick = {
                                currentScreen = "premium"
                            },
                            onBack = {
                                currentScreen = "home"
                            },
                            onItemClick = { result, type ->
                                analysisResult = result
                                selectedConversationType = type
                                currentScreen = "result"
                            }
                        )
                    }

                    "premium" -> {
                        PremiumScreen(
                            appLanguage = appLanguage,
                            isPremium = effectivePremium,
                            onMonthlyClick = {
                                billingManager.launchPurchase(
                                    activity = this@MainActivity,
                                    basePlanId = "monthly"
                                )
                            },
                            onYearlyClick = {
                                billingManager.launchPurchase(
                                    activity = this@MainActivity,
                                    basePlanId = "yearly"
                                )
                            },
                            onRestoreClick = {
                                billingManager.checkExistingPurchases()
                            },
                            onPdfBrandingClick = {
                                currentScreen = "pdfBranding"
                            },
                            onLanguageClick = {
                                currentScreen = "language"
                            },
                            onBack = {
                                currentScreen = "settings"
                            }
                        )
                    }

                    "language" -> {
                        LanguageSettingsScreen(
                            selectedLanguage = appLanguage,
                            onLanguageSelected = { languageCode ->
                                appLanguage = languageCode

                                scope.launch {
                                    languageStore.saveLanguage(languageCode)
                                }

                                Toast.makeText(
                                    context,
                                    if (languageCode == "tr") {
                                        "Dil Türkçe olarak kaydedildi."
                                    } else {
                                        "Language saved as English."
                                    },
                                    Toast.LENGTH_SHORT
                                ).show()

                                currentScreen = "settings"
                            },
                            onBack = {
                                currentScreen = "settings"
                            }
                        )
                    }

                    "pdfBranding" -> {
                        PdfBrandingScreen(
                            initialLogoUri = pdfBranding.logoUri,
                            initialCompanyName = pdfBranding.companyName,
                            initialPhone = pdfBranding.phone,
                            initialEmail = pdfBranding.email,
                            initialWebsite = pdfBranding.website,
                            initialAddress = pdfBranding.address,
                            onSave = {
                                    logoUri,
                                    companyName,
                                    phone,
                                    email,
                                    website,
                                    address ->

                                val updatedBranding = PdfBranding(
                                    logoUri = logoUri,
                                    companyName = companyName,
                                    phone = phone,
                                    email = email,
                                    website = website,
                                    address = address
                                )

                                pdfBranding = updatedBranding

                                scope.launch {
                                    pdfBrandingStore.saveBranding(
                                        updatedBranding
                                    )

                                    Toast.makeText(
                                        context,
                                        "PDF branding saved.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                currentScreen = "settings"
                            },
                            onBack = {
                                currentScreen = "settings"
                            }
                        )
                    }
                }
            }
        }
    }
}