package com.toxiclens.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.toxiclens.app.data.HistoryStore
import com.toxiclens.app.ui.AnalyzeScreen
import com.toxiclens.app.ui.ConversationTypeScreen
import com.toxiclens.app.ui.HistoryScreen
import com.toxiclens.app.ui.HomeScreen
import com.toxiclens.app.ui.ResultScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val context = LocalContext.current
                val historyStore = remember { HistoryStore(context) }
                val scope = rememberCoroutineScope()

                var currentScreen by remember { mutableStateOf("home") }
                var analysisResult by remember { mutableStateOf("") }
                var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
                var selectedConversationType by remember { mutableStateOf("❤️ Relationship") }

                when (currentScreen) {

                    "home" -> HomeScreen(
                        onAnalyzeClick = {
                            currentScreen = "analyze"
                        },
                        onHistoryClick = {
                            currentScreen = "history"
                        }
                    )

                    "analyze" -> AnalyzeScreen(
                        onBack = {
                            currentScreen = "home"
                        },
                        onImagesSelected = { uris ->
                            selectedImageUris = uris
                            currentScreen = "conversationType"
                        }
                    )

                    "conversationType" -> ConversationTypeScreen(
                        imageUris = selectedImageUris,
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

                    "result" -> ResultScreen(
                        result = analysisResult,
                        conversationType = selectedConversationType,
                        onBack = {
                            currentScreen = "conversationType"
                        }
                    )

                    "history" -> HistoryScreen(
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
            }
        }
    }
}