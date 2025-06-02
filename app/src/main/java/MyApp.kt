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
        // Ensure language is properly applied after app initialization
        try {
            val savedLanguage = LanguageManager.getLanguagePreferenceSync(applicationContext)
            Log.d(TAG, "Applying language in onCreate: $savedLanguage")
            LanguageManager.setLocale(applicationContext, savedLanguage)
        } catch (e: Exception) {
            Log.e(TAG, "Error applying language in onCreate", e)
        }
    }

    override fun attachBaseContext(base: Context) {
        try {
            val savedLanguage = LanguageManager.getLanguagePreferenceSync(base)
            Log.d(TAG, "Loading saved language: $savedLanguage")
            super.attachBaseContext(LanguageManager.updateResources(base, savedLanguage))
        } catch (e: Exception) {
            Log.e(TAG, "Error in attachBaseContext", e)
            super.attachBaseContext(base)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        try {
            applicationScope.launch {
                val savedLanguage =
                    LanguageManager.getLanguagePreference(applicationContext).first()
                LanguageManager.setLocale(applicationContext, savedLanguage)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onConfigurationChanged", e)
        }
    }
}
