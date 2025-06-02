package Utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Locale

object LanguageManager {
    private const val TAG = "LanguageManager"
    const val DEFAULT_LANGUAGE = "en"
    private var prefsManager: PreferencesManager? = null
    private const val LANGUAGE_PREF_KEY = "selected_language"

    private fun getPrefsManager(context: Context): PreferencesManager {
        if (prefsManager == null) {
            // Use context directly if applicationContext is null (during attachBaseContext)
            val contextToUse = context.applicationContext ?: context
            prefsManager = PreferencesManager(contextToUse, "language_prefs")
        }
        return prefsManager!!
    }

    fun getLanguages(): List<Language> =
        listOf(Language("en", "English"), Language("vi", "Tiếng Việt"))

    fun setLocale(context: Context, languageCode: String) {
        try {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)

            val config = context.resources.configuration
            config.setLocale(locale)

            // Always update the current context's resources
            context.resources.updateConfiguration(config, context.resources.displayMetrics)

            // Also update application context if different
            if (context != context.applicationContext) {
                context.applicationContext.resources.updateConfiguration(
                    config,
                    context.applicationContext.resources.displayMetrics
                )
            }

            // For API 24+, also create configuration context
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.createConfigurationContext(config)
            }

            Log.d(TAG, "Language set to: $languageCode")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting locale", e)
            setDefaultLocale(context)
        }
    }

    private fun setDefaultLocale(context: Context) {
        try {
            val locale = Locale(DEFAULT_LANGUAGE)
            Locale.setDefault(locale)
            val resources = context.resources
            val config = Configuration(resources.configuration)
            config.setLocale(locale)
            context.createConfigurationContext(config)
            resources.updateConfiguration(config, resources.displayMetrics)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting default locale", e)
        }
    }

    fun updateResources(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)

        // Update the context's resources regardless of API level
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(configuration)
        } else {
            context
        }
    }

    fun saveLanguagePreference(context: Context, languageCode: String) {
        try {
            getPrefsManager(context).setString(LANGUAGE_PREF_KEY, languageCode)
            setLocale(context, languageCode)
            Log.d(TAG, "Language preference saved: $languageCode")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving language preference", e)
        }
    }

    fun getLanguagePreference(context: Context): Flow<String> = flow {
        val savedLanguage = getPrefsManager(context).getString(LANGUAGE_PREF_KEY, DEFAULT_LANGUAGE)
        emit(savedLanguage)
    }

    // Add this synchronous method for attachBaseContext
    fun getLanguagePreferenceSync(context: Context): String {
        return try {
            getPrefsManager(context).getString(LANGUAGE_PREF_KEY, DEFAULT_LANGUAGE)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting language preference sync", e)
            DEFAULT_LANGUAGE
        }
    }

}

data class Language(val code: String, val name: String)

