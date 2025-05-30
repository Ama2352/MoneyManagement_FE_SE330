package DI.API.TokenHandler

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.JWTDecodeException

object AuthStorage {
    private const val PREFS_NAME = "auth_prefs"
    private const val TOKEN_KEY = "auth_token"

    private fun getEncryptedPrefs(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun storeToken(context: Context, token: String) {
        getEncryptedPrefs(context).edit().putString(TOKEN_KEY, token).apply()
        Log.d("AuthStorage", "Stored token: $token")
    }

    fun getToken(context: Context): String? {
        return getEncryptedPrefs(context).getString(TOKEN_KEY, null)
    }

    fun clearToken(context: Context) {
        getEncryptedPrefs(context).edit().remove(TOKEN_KEY).apply()
        Log.d("AuthStorage", "Cleared token")
    }

    // Decode token and get userId
    fun getUserIdFromToken(context: Context): String? {
        val token = getToken(context) ?: return null
        return try {
            // Decode JWT token and extract the userId from the "sub" claim (or any claim your backend uses)
            val decodedJWT = JWT.decode(token)
            decodedJWT.getClaim("sub").asString()  // replace "sub" if your token uses a different claim
        } catch (e: JWTDecodeException) {
            null
        }
    }
}