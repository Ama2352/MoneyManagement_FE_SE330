package DI.API.TokenHandler

import android.content.Context
import android.util.Base64
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    @ApplicationContext private val context: Context
) : Interceptor {

    companion object {
        private val _tokenExpiredFlow = MutableSharedFlow<Unit>(
            replay = 0,
            extraBufferCapacity = 1)
        val tokenExpiredFlow = _tokenExpiredFlow.asSharedFlow()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = AuthStorage.getToken(context)
        Log.d("AuthInterceptor", "Using token jti: ${token?.let { parseJti(it) } ?: "null"}")
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        val response = chain.proceed(request)
        Log.d("AuthInterceptor", "Response code: ${response.code} for URL: ${chain.request().url}")
        if (response.code == 401 || response.code == 403) {
            Log.d("AuthInterceptor", "Token expired! Response code: ${response.code}")
            _tokenExpiredFlow.tryEmit(Unit)
        }
        return response
    }

    private fun parseJti(token: String): String? {
        return try {
            val payload = token.split(".")[1]
            val decoded = String(Base64.decode(payload, Base64.URL_SAFE))
            decoded.substringAfter("\"jti\":\"").substringBefore("\"")
        } catch (e: Exception) {
            "unknown"
        }
    }
}