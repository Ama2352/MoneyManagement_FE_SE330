package DI.Repositories

import DI.API.ExchangeApiService
import DI.Models.Currency.CurrencyRates
import DI.Models.Currency.CurrencyPreference
import DI.Utils.CurrencyUtils
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@Singleton
class CurrencyRepository @Inject constructor(
    private val exchangeApiService: ExchangeApiService,
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("currency_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val TAG = "CurrencyRepository"
        private const val PREF_IS_VND = "is_vnd"
        private const val PREF_USD_TO_VND_RATE = "usd_to_vnd_rate"
        private const val PREF_LAST_UPDATED = "last_updated"
        private const val DEFAULT_USD_TO_VND = 24000.0 // Fallback rate
        private const val CACHE_DURATION = 60 * 60 * 1000L // 1 hour in milliseconds
    }

    suspend fun getExchangeRates(): Result<CurrencyRates> {
        return try {
            // Check if we have cached rates that are still valid
            val cachedRates = getCachedRates()
            if (cachedRates != null && isRatesValid(cachedRates)) {
                Log.d(TAG, "Using cached exchange rates")
                return Result.success(cachedRates)
            }
            Log.d(TAG, "Fetching fresh exchange rates from API")
            val response = exchangeApiService.getExchangeRates()
            if (response.isSuccessful) {
                val exchangeResponse = response.body()
                val vndRate = exchangeResponse?.usd?.get("vnd") ?: DEFAULT_USD_TO_VND
                
                val rates = CurrencyRates(
                    usdToVnd = vndRate, // 1 USD = vndRate VND
                    vndToUsd = 1.0 / vndRate, // 1 VND = (1/vndRate) USD
                    lastUpdated = System.currentTimeMillis()
                )
                
                // Cache the rates
                cacheRates(rates)
                Log.d(TAG, "Exchange rates updated: 1 USD = ${rates.usdToVnd} VND")
                
                Result.success(rates)
            } else {
                Log.w(TAG, "Failed to fetch exchange rates: ${response.code()}")
                // Return cached rates if available, otherwise default
                Result.success(getCachedRates() ?: getDefaultRates())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching exchange rates", e)
            // Return cached rates if available, otherwise default
            Result.success(getCachedRates() ?: getDefaultRates())
        }
    }

    private fun getCachedRates(): CurrencyRates? {
        val usdToVndRate = prefs.getFloat(PREF_USD_TO_VND_RATE, -1f)
        val lastUpdated = prefs.getLong(PREF_LAST_UPDATED, 0L)
        
        return if (usdToVndRate > 0) {
            CurrencyRates(
                usdToVnd = usdToVndRate.toDouble(),
                vndToUsd = 1.0 / usdToVndRate.toDouble(),
                lastUpdated = lastUpdated
            )
        } else {
            null
        }
    }

    private fun isRatesValid(rates: CurrencyRates): Boolean {
        return System.currentTimeMillis() - rates.lastUpdated < CACHE_DURATION
    }

    private fun cacheRates(rates: CurrencyRates) {
        prefs.edit {
            putFloat(PREF_USD_TO_VND_RATE, rates.usdToVnd.toFloat())
                .putLong(PREF_LAST_UPDATED, rates.lastUpdated)
        }
    }

    private fun getDefaultRates(): CurrencyRates {
        return CurrencyRates(
            usdToVnd = DEFAULT_USD_TO_VND,
            vndToUsd = 1.0 / DEFAULT_USD_TO_VND,
            lastUpdated = System.currentTimeMillis()
        )
    }

    fun getCurrencyPreference(): CurrencyPreference {
        val isVND = prefs.getBoolean(PREF_IS_VND, true)
        return CurrencyPreference(isVND)
    }

    fun setCurrencyPreference(preference: CurrencyPreference) {
        prefs.edit {
            putBoolean(PREF_IS_VND, preference.isVND)
        }
        Log.d(TAG, "Currency preference updated: ${if (preference.isVND) "VND" else "USD"}")
    }

    // Convert amount from USD to VND for database storage
    suspend fun convertUsdToVnd(usdAmount: Double): Double {
        val rates = getExchangeRates().getOrNull() ?: getDefaultRates()
        return CurrencyUtils.usdToVnd(usdAmount, rates.usdToVnd)
    }

    // Convert amount from VND to USD for display
    suspend fun convertVndToUsd(vndAmount: Double): Double {
        val rates = getExchangeRates().getOrNull() ?: getDefaultRates()
        return CurrencyUtils.vndToUsd(vndAmount, rates.vndToUsd)
    }
}
