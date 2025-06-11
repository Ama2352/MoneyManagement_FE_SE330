package com.example.moneymanagement_frontend

import DI.API.CrashHandler.CrashHandler
import DI.Navigation.AppNavHost
import Utils.BaseActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import theme.MoneyManagementTheme

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Crash handler
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler(this))
        
        setContent {
            MoneyManagementTheme {
                AppNavHost()
            }
        }
    }
}


