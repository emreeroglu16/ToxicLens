package com.toxiclens.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.toxiclens.app.ui.AnalyzeScreen
import com.toxiclens.app.ui.HomeScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme {

                var currentScreen by remember {
                    mutableStateOf("home")
                }

                when (currentScreen) {

                    "home" -> HomeScreen(
                        onAnalyzeClick = {
                            currentScreen = "analyze"
                        }
                    )

                    "analyze" -> AnalyzeScreen(
                        onBack = {
                            currentScreen = "home"
                        }
                    )
                }
            }
        }
    }
}