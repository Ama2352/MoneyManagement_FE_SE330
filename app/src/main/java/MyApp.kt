package DI

import Utils.LanguageManager
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltAndroidApp
class MyApp : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    companion object {
        private const val TAG = "MoneyManagementApp"
    }
    
    override fun onCreate() {
        super.onCreate()
        initializeLanguage()
    }
    
    override fun attachBaseContext(base: Context) {
        try {
            val languageCode = runCatching {
                base.resources.configuration.locales[0].language
            }.getOrDefault("en")
            
            super.attachBaseContext(LanguageManager.updateResources(base, languageCode))
        } catch (e: Exception) {
            Log.e(TAG, "Error in attachBaseContext", e)
            super.attachBaseContext(base)
        }
    }

    private fun initializeLanguage() {
        applicationScope.launch {
            try {
                val savedLanguage = LanguageManager.getLanguagePreference(applicationContext).first()
                LanguageManager.setLocale(applicationContext, savedLanguage)
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing language", e)
                setDefaultLanguage()
            }
        }
    }

    private fun setDefaultLanguage() {
        try {
            val defaultLanguage = "en"
            LanguageManager.setLocale(applicationContext, defaultLanguage)
            applicationScope.launch {
                LanguageManager.saveLanguagePreference(applicationContext, defaultLanguage)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting default language", e)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        try {
            applicationScope.launch {
                val savedLanguage = LanguageManager.getLanguagePreference(applicationContext).first()
                LanguageManager.setLocale(applicationContext, savedLanguage)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onConfigurationChanged", e)
        }
    }
}

