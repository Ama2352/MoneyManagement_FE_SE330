package Utils

import android.content.Context
import androidx.activity.ComponentActivity

/**
 * Base activity that automatically applies the saved language preference.
 * All activities should extend this to ensure consistent language behavior.
 */
abstract class BaseActivity : ComponentActivity() {
    
    override fun attachBaseContext(base: Context) {
        val savedLanguage = LanguageManager.getLanguagePreferenceSync(base)
        super.attachBaseContext(LanguageManager.updateResources(base, savedLanguage))
    }
}
