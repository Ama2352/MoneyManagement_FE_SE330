package Utils

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import DI.Models.Currency.CurrencyRates
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// Entry point for accessing MessageTranslationUtils
@EntryPoint
@InstallIn(SingletonComponent::class)
interface MessageUtilsEntryPoint {
    fun messageTranslationUtils(): MessageTranslationUtils
}

/**
 * Reusable composable hook for handling message translation and currency formatting
 * This can be used in any composable that needs to display messages with currency amounts
 * 
 * @param message The original message
 * @param isVND User's currency preference
 * @param exchangeRates Current exchange rates
 * @return Formatted and translated message
 */
@Composable
fun useTranslatedMessage(
    message: String,
    isVND: Boolean,
    exchangeRates: CurrencyRates?
): String {
    val context = LocalContext.current
    
    // Get MessageTranslationUtils through EntryPoint
    val messageTranslationUtils = remember {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            MessageUtilsEntryPoint::class.java
        )
        hiltEntryPoint.messageTranslationUtils()
    }
    
    // State to hold the formatted and translated message
    var formattedMessage by remember { mutableStateOf(message) }
    
    // Update message when any dependency changes
    LaunchedEffect(message, isVND, exchangeRates) {
        formattedMessage = messageTranslationUtils.formatAndTranslateMessage(
            context = context,
            message = message,
            isVND = isVND,
            exchangeRates = exchangeRates
        )
    }
    
    return formattedMessage
}

/**
 * Simplified version for currency-only formatting without translation
 */
@Composable
fun useFormattedCurrencyMessage(
    message: String,
    isVND: Boolean,
    exchangeRates: CurrencyRates?
): String {
    val context = LocalContext.current
    
    val messageTranslationUtils = remember {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            MessageUtilsEntryPoint::class.java
        )
        hiltEntryPoint.messageTranslationUtils()
    }
    
    return remember(message, isVND, exchangeRates) {
        messageTranslationUtils.formatCurrencyInMessage(message, isVND, exchangeRates)
    }
}

/**
 * Translation-only version without currency formatting
 */
@Composable
fun useTranslatedMessageOnly(message: String): String {
    val context = LocalContext.current
    
    val messageTranslationUtils = remember {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            MessageUtilsEntryPoint::class.java
        )
        hiltEntryPoint.messageTranslationUtils()
    }
    
    var translatedMessage by remember { mutableStateOf(message) }
    
    LaunchedEffect(message) {
        translatedMessage = messageTranslationUtils.translateMessage(context, message)
    }
    
    return translatedMessage
}
