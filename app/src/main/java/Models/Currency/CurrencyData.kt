package DI.Models.Currency

import com.google.gson.annotations.SerializedName

// API Response for exchange rates (USD base currency)
data class ExchangeRatesResponse(
    val date: String,
    val usd: Map<String, Double>
)

// User currency preference
data class CurrencyPreference(
    val isVND: Boolean = true
)

// Currency conversion rates
data class CurrencyRates(
    val usdToVnd: Double,
    val vndToUsd: Double,
    val lastUpdated: Long = System.currentTimeMillis()
)

// Currency display formatting
data class CurrencyDisplay(
    val amount: Double,
    val formattedAmount: String,
    val currencyCode: String,
    val symbol: String
)
