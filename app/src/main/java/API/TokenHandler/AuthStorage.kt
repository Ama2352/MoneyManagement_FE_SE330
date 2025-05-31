package DI.API.TokenHandler

import android.content.Context
import android.util.Log
import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.JWTDecodeException
import Utils.SecureStorage

object AuthStorage {
    private const val TOKEN_KEY = "auth_token"
    private const val TAG = "AuthStorage"
    private var secureStorage: SecureStorage? = null

    private fun getStorage(context: Context): SecureStorage {
        if (secureStorage == null) {
            secureStorage = SecureStorage(context.applicationContext)
        }
        return secureStorage!!
    }

    fun storeToken(context: Context, token: String) {
        try {
            getStorage(context).saveSecureString(TOKEN_KEY, token)
            Log.d(TAG, "Stored token successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error storing token", e)
        }
    }

    fun getToken(context: Context): String? {
        return try {
            val token = getStorage(context).getSecureString(TOKEN_KEY, "")
            if (token.isEmpty()) null else token
        } catch (e: Exception) {
            Log.e(TAG, "Error getting token", e)
            null
        }
    }

    fun clearToken(context: Context) {
        try {
            getStorage(context).clearAll()
            Log.d(TAG, "Cleared token")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing token", e)
        }
    }

    // Decode token and get userId
    fun getUserIdFromToken(context: Context): String? {
        val token = getToken(context) ?: return null
        return try {
            // Decode JWT token and extract the userId from the "sub" claim (or any claim your backend uses)
            val decodedJWT = JWT.decode(token)
            decodedJWT.getClaim("sub").asString()  // replace "sub" if your token uses a different claim
        } catch (e: JWTDecodeException) {
            Log.e(TAG, "Error decoding JWT", e)
            null
        }
    }
}