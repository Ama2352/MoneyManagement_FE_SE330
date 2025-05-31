package Utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class PreferencesManager(context: Context, name: String) {
    private val prefs: SharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    
    companion object {
        private const val TAG = "PreferencesManager"
    }

    fun getString(key: String, defaultValue: String): String {
        return try {
            prefs.getString(key, defaultValue) ?: defaultValue
        } catch (e: Exception) {
            Log.e(TAG, "Error reading string preference", e)
            defaultValue
        }
    }

    fun setString(key: String, value: String) {
        try {
            prefs.edit().putString(key, value).commit() // Using commit() instead of apply() for immediate write
            Log.d(TAG, "Saved preference: $key = $value")
        } catch (e: Exception) {
            Log.e(TAG, "Error writing string preference", e)
        }
    }

    fun getStringFlow(key: String, defaultValue: String): Flow<String> = callbackFlow {
        // Emit initial value
        trySend(getString(key, defaultValue))

        // Listen for changes
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == key) {
                trySend(getString(key, defaultValue))
            }
        }

        prefs.registerOnSharedPreferenceChangeListener(listener)

        // Cleanup when Flow is cancelled
        awaitClose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    fun clearAll() {
        try {
            prefs.edit().clear().commit()
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing preferences", e)
        }
    }
} 