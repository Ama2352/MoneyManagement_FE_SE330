package com.example.moneymanagement_frontend

import DI.API.CrashHandler.CrashHandler
import DI.Navigation.AppNavHost
import Utils.BaseActivity
import android.graphics.Color
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import theme.MoneyManagementTheme

@AndroidEntryPoint
class MainActivity : BaseActivity() {
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


