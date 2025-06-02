package API.TokenHandler

import Utils.LanguageManager
import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.Response

@Singleton
class LanguageInterceptor @Inject constructor(
    @ApplicationContext private val context: Context
) : Interceptor {

    companion object {
        private const val TAG = "LanguageInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Get saved language preference instead of system locale
        val savedLanguage = LanguageManager.getLanguagePreferenceSync(context)
        val languageTag =
                when (savedLanguage) {
                    "vi" -> "vi-VN"
                    "en" -> "en-US"
                    else -> "en-US" // Default fallback
                }

        Log.d(TAG, "Using language: $savedLanguage -> $languageTag")

        // Add Accept-Language header to the request
        val newRequest =
                originalRequest.newBuilder().addHeader("Accept-Language", languageTag).build()

        return chain.proceed(newRequest)
    }
}
