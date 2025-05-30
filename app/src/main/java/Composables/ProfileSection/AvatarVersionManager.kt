package DI.Composables.ProfileSection

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private const val DATASTORE_NAME = "user_prefs"
private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

object AvatarVersionManager {
    private val AVATAR_VERSION_KEY = stringPreferencesKey("avatar_version")

    fun getAvatarVersion(context: Context): Flow<String> {
        return context.dataStore.data.map { prefs ->
            prefs[AVATAR_VERSION_KEY] ?: "v1"
        }
    }

    suspend fun setAvatarVersion(context: Context, version: String) {
        context.dataStore.edit { prefs ->
            prefs[AVATAR_VERSION_KEY] = version
        }
    }
}