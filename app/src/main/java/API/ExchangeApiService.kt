package DI.API

import DI.Models.Currency.ExchangeRatesResponse
import retrofit2.Response
import retrofit2.http.GET

interface ExchangeApiService {
    // Exchange API endpoint for USD rates (USD as base currency)
    @GET("v1/currencies/usd.json")
    suspend fun getExchangeRates(): Response<ExchangeRatesResponse>
}
