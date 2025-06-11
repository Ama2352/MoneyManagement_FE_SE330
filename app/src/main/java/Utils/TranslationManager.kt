package Utils

import android.content.Context
import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranslationManager @Inject constructor() {
    private val TAG = "TranslationManager"
    private var translator: Translator? = null
    private var isInitialized = false
    
    suspend fun translateMessage(
        context: Context,
        message: String
    ): String {
        val languageCode = LanguageManager.getLanguagePreferenceSync(context)
        
        return if (languageCode == "vi" && !isVietnamese(message)) {
            // Check cache first
            val cachedTranslation = getCachedTranslation(context, message)
            if (cachedTranslation != null) {
                cachedTranslation
            } else {
                // Initialize translator if needed
                if (!isInitialized) {
                    initializeTranslator()
                }
                
                // Translate and cache
                try {
                    val translated = translator?.translate(message)?.await() ?: message
                    cacheTranslation(context, message, translated)
                    translated
                } catch (e: Exception) {
                    Log.e(TAG, "Translation failed", e)
                    message // Return original message if translation fails
                }
            }
        } else {
            message
        }
    }
    
    private suspend fun initializeTranslator() {
        try {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.VIETNAMESE)
                .build()
            
            translator = Translation.getClient(options)
            
            val conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()
            
            translator?.downloadModelIfNeeded(conditions)?.await()
            isInitialized = true
            Log.d(TAG, "Translation model ready")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize translator", e)
            isInitialized = false
        }
    }
    
    private fun isVietnamese(text: String): Boolean {
        val vietnameseChars = "àáảãạăắằẳẵặâấầẩẫậèéẻẽẹêếềểễệìíỉĩịòóỏõọôốồổỗộơớờởỡợùúủũụưứừửữựỳýỷỹỵđ"
        return text.any { it.lowercase() in vietnameseChars }
    }
    
    private fun getCachedTranslation(context: Context, original: String): String? {
        return try {
            val sharedPref = context.getSharedPreferences("translation_cache", Context.MODE_PRIVATE)
            sharedPref.getString("trans_${original.hashCode()}", null)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting cached translation", e)
            null
        }
    }
    
    private fun cacheTranslation(context: Context, original: String, translated: String) {
        try {
            val sharedPref = context.getSharedPreferences("translation_cache", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("trans_${original.hashCode()}", translated)
                apply()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error caching translation", e)
        }
    }
    
    fun cleanup() {
        translator?.close()
        translator = null
        isInitialized = false
    }
}
