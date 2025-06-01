package DI.Composables.ProfileSection

import android.content.Context
import Utils.PreferencesManager
import kotlinx.coroutines.flow.Flow

object AvatarVersionManager {
    private const val AVATAR_VERSION_KEY = "avatar_version"
    private var prefsManager: PreferencesManager? = null

    private fun getPrefsManager(context: Context): PreferencesManager {
        if (prefsManager == null) {
            prefsManager = PreferencesManager(context.applicationContext, "avatar_prefs")
        }
        return prefsManager!!
    }

    fun getAvatarVersion(context: Context): Flow<String> {
        return getPrefsManager(context).getStringFlow(AVATAR_VERSION_KEY, "v1")
    }

    suspend fun setAvatarVersion(context: Context, version: String) {
        getPrefsManager(context).setString(AVATAR_VERSION_KEY, version)
    }
}