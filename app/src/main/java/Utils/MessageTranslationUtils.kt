package Utils

import android.content.Context
import DI.Models.Currency.CurrencyRates
import DI.Utils.CurrencyUtils
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageTranslationUtils @Inject constructor(
    private val translationManager: TranslationManager
) {
      /**
     * Formats currency amounts in a message and translates it based on user preference
     * This is a reusable function that can be used throughout the app
     * 
     * @param context Android context
     * @param message The original message containing currency amounts (e.g., "You need to save $500,000")
     * @param isVND User's currency preference (true for VND, false for USD)
     * @param exchangeRates Current exchange rates for conversion
     * @return Formatted and translated message
     */
    suspend fun formatAndTranslateMessage(
        context: Context,
        message: String,
        isVND: Boolean,
        exchangeRates: CurrencyRates?
    ): String {
        val TAG = "MessageTranslationUtils"
        
        // Step 1: Handle currency conversion first
        val currencyFormattedMessage = formatCurrencyInMessage(message, isVND, exchangeRates)
        Log.d(TAG, "formatAndTranslateMessage - After currency formatting: '$currencyFormattedMessage'")
        
        // Step 2: Protect currency symbols before translation
        val (protectedMessage, symbolMap) = protectCurrencySymbols(currencyFormattedMessage)
        Log.d(TAG, "formatAndTranslateMessage - After protecting symbols: '$protectedMessage'")
        Log.d(TAG, "formatAndTranslateMessage - Symbol map: $symbolMap")
        
        // Step 3: Translate the message with protected symbols
        val translatedMessage = translationManager.translateMessage(context, protectedMessage)
        Log.d(TAG, "formatAndTranslateMessage - After translation: '$translatedMessage'")
        
        // Step 4: Restore currency symbols
        val finalMessage = restoreCurrencySymbols(translatedMessage, symbolMap)
        Log.d(TAG, "formatAndTranslateMessage - Final message: '$finalMessage'")
        
        return finalMessage
    }

    /**
     * Formats only currency amounts in a message without translation
     * Useful when you only need currency conversion without language translation
     */
    fun formatCurrencyInMessage(
        message: String,
        isVND: Boolean,
        exchangeRates: CurrencyRates?
    ): String {
        val TAG = "MessageTranslationUtils"

        // Log the original message
        Log.d(TAG, "formatCurrencyInMessage - Original message: '$message'")
        Log.d(TAG, "formatCurrencyInMessage - isVND: $isVND")
        Log.d(TAG, "formatCurrencyInMessage - exchangeRates: $exchangeRates")

        // Regex to match raw decimal numbers (e.g., 50000.00, 10000.00, 1000.50)
        // But exclude percentages (numbers followed by %)
        val currencyRegex = """(\b\d+\.\d{2}\b)(?!\s*%)""".toRegex()

        // Find all matches first for debugging
        val matches = currencyRegex.findAll(message).toList()
        Log.d(TAG, "formatCurrencyInMessage - Found ${matches.size} matches:")
        matches.forEachIndexed { index, match ->
            Log.d(TAG, "  Match $index: '${match.value}' at position ${match.range}")
        }

        if (matches.isEmpty()) {
            Log.w(TAG, "formatCurrencyInMessage - No currency amounts found in message")
            return message
        }

        val result = currencyRegex.replace(message) { matchResult ->
            val amountStr = matchResult.groupValues[1] // Extract "50000.00"
            val amount = amountStr.toDoubleOrNull() ?: 0.0 // Convert to 50000.0

            Log.d(TAG, "formatCurrencyInMessage - Processing amount: '$amountStr' -> $amount")

            // Convert and format the amount based on user preference
            val convertedAmount = if (isVND) {
                Log.d(TAG, "formatCurrencyInMessage - Keeping VND amount: $amount")
                amount // Keep as VND (50000.0)
            } else {
                // Convert VND to USD
                val rate = exchangeRates?.usdToVnd ?: 24000.0
                val converted = CurrencyUtils.vndToUsd(amount, rate)
                Log.d(TAG, "formatCurrencyInMessage - Converting VND to USD: $amount VND -> $converted USD (rate: $rate)")
                converted
            }

            // Format the amount with proper currency symbol and formatting
            val formatted = CurrencyUtils.formatAmount(convertedAmount, isVND)
            Log.d(TAG, "formatCurrencyInMessage - Formatted amount: '$formatted'")

            formatted
        }

        Log.d(TAG, "formatCurrencyInMessage - Final result: '$result'")
        return result
    }
      /**
     * Translates a message without currency formatting
     * Useful when you only need translation without currency conversion
     */
    suspend fun translateMessage(
        context: Context,
        message: String
    ): String {
        return translationManager.translateMessage(context, message)
    }
    
    /**
     * Protects currency symbols by replacing them with placeholders before translation
     * Returns the protected message and a map of placeholders to original symbols
     */
    private fun protectCurrencySymbols(message: String): Pair<String, Map<String, String>> {
        val symbolMap = mutableMapOf<String, String>()
        var protectedMessage = message
        
        // Define currency symbols and their placeholders
        val currencyReplacements = mapOf(
            "₫" to "VNDCURRENCY",
            "$" to "USDCURRENCY"
        )
        
        currencyReplacements.forEach { (symbol, placeholder) ->
            if (protectedMessage.contains(symbol)) {
                symbolMap[placeholder] = symbol
                protectedMessage = protectedMessage.replace(symbol, placeholder)
            }
        }
        
        return Pair(protectedMessage, symbolMap)
    }
    
    /**
     * Restores currency symbols from placeholders after translation
     */
    private fun restoreCurrencySymbols(message: String, symbolMap: Map<String, String>): String {
        var restoredMessage = message
        
        symbolMap.forEach { (placeholder, symbol) ->
            restoredMessage = restoredMessage.replace(placeholder, symbol)
        }
        
        return restoredMessage
    }
}
