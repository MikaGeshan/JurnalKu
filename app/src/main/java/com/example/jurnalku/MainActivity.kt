package com.example.jurnalku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.example.jurnalku.ui.MainScreen
import com.example.jurnalku.ui.theme.JurnalKuTheme
import androidx.compose.material3.Surface
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.jurnalku.ui.theme.SoftGreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            JurnalKuTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SoftGreen
                ) {
                    MainScreen()
                }

            }
        }
    }
}