package com.example.moneymanagement_frontend

import DI.API.CrashHandler.CrashHandler
import DI.Navigation.AppNavHost
import ViewModels.AuthViewModel
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import theme.MoneyManagementTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Crash handler
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(this))

        // Set navigation bar to black (Color.BLACK.toArgb() for Compose)
        window.navigationBarColor = Color.BLACK // API 21+ :contentReference[oaicite:2]{index=2}

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MoneyManagementTheme {
                AppNavHost()
            }
        }
    }
}


