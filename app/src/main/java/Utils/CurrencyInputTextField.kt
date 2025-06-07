package DI.Utils

import android.util.Log
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue

/**
 * A reusable currency input TextField component that handles both VND and USD currencies
 * with proper formatting, validation, and user experience optimizations.
 *
 * Features:
 * - VND: Live formatting while typing with cursor at end
 * - USD: Natural typing with formatting on focus loss, 2 decimal place validation
 * - Smart cursor positioning
 * - Input validation with warning messages
 * - Customizable placeholder text
 * - Optional label and supporting text
 *
 * @param value The current TextFieldValue state
 * @param onValueChange Callback when the value changes
 * @param isVND Whether the current currency is VND (true) or USD (false)
 * @param modifier Modifier to be applied to the TextField
 * @param label Optional label text
 * @param placeholder Optional placeholder text (defaults to currency-specific examples)
 * @param supportingText Optional supporting text (will be overridden by validation warnings)
 * @param enabled Whether the TextField is enabled
 * @param isError Whether to show error state
 * @param colors TextField colors
 * @param onFormatted Optional callback when text is formatted (provides formatted text and parsed amount)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyInputTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    isVND: Boolean,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    onFormatted: ((formattedText: String, parsedAmount: Double?) -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    var showDecimalWarning by remember { mutableStateOf(false) }
    
    // Default placeholder based on currency
    val defaultPlaceholder = if (isVND) "e.g. 1.000.000₫" else "e.g. 1000.51 (formats when done)"
    
    OutlinedTextField(
        value = value,
        onValueChange = { newTextFieldValue ->
            if (isVND) {
                // VND: Live formatting while typing
                val inputText = newTextFieldValue.text
                if (inputText.isEmpty()) {
                    onValueChange(newTextFieldValue)
                    onFormatted?.invoke("", null)
                    return@OutlinedTextField
                }

                val rawMoney = CurrencyUtils.parseAmount(inputText)
                if (rawMoney != null && rawMoney > 0) {
                    val formattedText = CurrencyUtils.formatAmount(rawMoney, isVND)
                    val cursorPosition = formattedText.lastIndexOf('₫').takeIf { it != -1 } ?: formattedText.length
                    val formattedValue = TextFieldValue(
                        text = formattedText,
                        selection = TextRange(cursorPosition)
                    )
                    onValueChange(formattedValue)
                    onFormatted?.invoke(formattedText, rawMoney)
                } else {
                    onValueChange(newTextFieldValue)
                    onFormatted?.invoke(newTextFieldValue.text, null)
                }
            } else {
                // USD: Validate decimal places and allow natural typing
                val newText = newTextFieldValue.text
                val decimalIndex = newText.lastIndexOf('.')

                // Update warning state
                if (decimalIndex != -1) {
                    val decimalPart = newText.substring(decimalIndex + 1)
                    showDecimalWarning = decimalPart.length > 2

                    if (decimalPart.length > 2) {
                        // Don't update the text field if exceeding decimal limit
                        return@OutlinedTextField
                    }
                } else {
                    showDecimalWarning = false
                }

                onValueChange(newTextFieldValue)
                val parsedAmount = CurrencyUtils.parseAmount(newText)
                onFormatted?.invoke(newText, parsedAmount)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier
            .onFocusChanged { focusState ->
                val wasFocused = isFocused
                isFocused = focusState.isFocused

                // USD formatting ONLY on focus loss
                if (!isVND && wasFocused && !focusState.isFocused && value.text.isNotEmpty()) {
                    val parsed = CurrencyUtils.parseAmount(value.text)
                    Log.d("CurrencyInputTextField", "USD Focus Loss - Input: '${value.text}', Parsed: $parsed")
                    if (parsed != null) {
                        val formattedText = CurrencyUtils.formatUSD(parsed)
                        Log.d("CurrencyInputTextField", "USD Focus Loss - Formatted: '$formattedText'")
                        val formattedValue = TextFieldValue(
                            text = formattedText,
                            selection = TextRange(formattedText.length)
                        )
                        onValueChange(formattedValue)
                        onFormatted?.invoke(formattedText, parsed)
                    }
                }

                // VND formatting on focus loss (for consistency)
                if (isVND && wasFocused && !focusState.isFocused && value.text.isNotEmpty()) {
                    val parsed = CurrencyUtils.parseAmount(value.text)
                    if (parsed != null) {
                        val formattedText = CurrencyUtils.formatAmount(parsed, true)
                        val finalPosition = formattedText.lastIndexOf('₫').takeIf { it != -1 } ?: formattedText.length
                        val formattedValue = TextFieldValue(
                            text = formattedText,
                            selection = TextRange(finalPosition)
                        )
                        onValueChange(formattedValue)
                        onFormatted?.invoke(formattedText, parsed)
                    }
                }
            },
        label = label?.let { { Text(it) } },
        placeholder = { Text(placeholder ?: defaultPlaceholder) },
        supportingText = {
            // Show decimal warning for USD, or custom supporting text
            when {
                !isVND && showDecimalWarning -> {
                    Text(
                        "USD amounts support only 2 decimal places",
                        color = MaterialTheme.colorScheme.error
                    )
                }                supportingText != null -> {
                    Text(supportingText)
                }
            }
        },
        enabled = enabled,
        isError = isError || (!isVND && showDecimalWarning),
        colors = colors
    )
}

/**
 * Preview component for USD input while typing (shows formatted preview)
 * Use this when isFocused = true and currency is USD
 */
@Composable
fun USDInputPreview(
    inputText: String,
    modifier: Modifier = Modifier
) {
    if (inputText.isNotEmpty()) {
        val parsed = CurrencyUtils.parseAmount(inputText)
        if (parsed != null) {
            val preview = CurrencyUtils.formatUSD(parsed)
            Text(
                text = "Preview: $preview",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = modifier
            )
        }
    }
}
