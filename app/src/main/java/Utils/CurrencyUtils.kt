package DI.Utils

import android.util.Log
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

object CurrencyUtils {
    
    private val vndFormatter: DecimalFormat = (DecimalFormat.getInstance(Locale("vi", "VN")) as DecimalFormat).apply {
        applyPattern("#,###")
        decimalFormatSymbols = decimalFormatSymbols.apply {
            groupingSeparator = '.'
        }
    }

    private val usdFormatter: DecimalFormat = (DecimalFormat.getInstance(Locale.US) as DecimalFormat).apply {
        applyPattern("#,##0.00")
    }
      /**
     * Format VND amount with proper thousands separator
     */
    fun formatVND(amount: Double): String {
        return "${vndFormatter.format(amount)}₫"
    }
    
    /**
     * Format USD amount with proper decimal places
     */
    fun formatUSD(amount: Double): String {
        return "$${usdFormatter.format(amount)}"
    }
    
    /**
     * Parse amount string removing currency symbols and separators
     * Handles both USD format (1,234.56) and VND format (1.234.567)
     */
    fun parseAmount(amountString: String): Double? {
        return try {
            val cleaned = amountString
                .replace("₫", "")
                .replace("$", "")
                .trim()

            Log.d("CurrencyUtils", "Parsing amount: $cleaned")

            when {
                // Detect USD format: period followed by 1-2 digits at the end
                cleaned.matches(Regex(".*\\.[0-9]{1,2}$")) -> {
                    Log.d("CurrencyUtils", "Detected USD format: $cleaned")
                    cleaned.replace(",", "").toDoubleOrNull()
                }

                // Detect VND format: multiple periods or period not at decimal position
                cleaned.contains(".") -> {
                    Log.d("CurrencyUtils", "Detected VND format: $cleaned")
                    cleaned.replace(".", "").toDoubleOrNull()
                }

                // No periods: simple number, might have commas as thousand separators
                else -> {
                    Log.d("CurrencyUtils", "Simple number format: $cleaned")
                    cleaned.replace(",", "").toDoubleOrNull()
                }
            }
        } catch (e: Exception) {
            Log.d("CurrencyUtils", "Error parsing amount: ${e.message}")
            null
        }
    }
    
    /**
     * Convert VND to USD
     */
    fun vndToUsd(vndAmount: Double, exchangeRate: Double): Double {
        return vndAmount / exchangeRate
    }
    
    /**
     * Convert USD to VND
     */
    fun usdToVnd(usdAmount: Double, exchangeRate: Double): Double {
        return usdAmount * exchangeRate
    }
    
    /**
     * Format amount based on currency type
     */
    fun formatAmount(amount: Double, isVND: Boolean): String {
        return if (isVND) {
            formatVND(amount)
        } else {
            formatUSD(amount)
        }
    }    
    
    /**
     * Format amount in compact form (k, M, B) based on currency type
     */
    fun formatCompactAmount(amount: Double, isVND: Boolean): String {
        val (value, suffix) = when {
            amount >= 1_000_000_000 -> Pair(amount / 1_000_000_000, "B")
            amount >= 1_000_000 -> Pair(amount / 1_000_000, "M")
            amount >= 1_000 -> Pair(amount / 1_000, "k")
            else -> Pair(amount, "")
        }
        
        val formattedNumber = if (isVND) {
            // VND: Use period as thousands separator, no decimal places for compact numbers
            if (suffix.isEmpty()) {
                // For small numbers, use full VND formatting with periods
                vndFormatter.format(value)
            } else {
                // For compact numbers, format with 1 decimal place and replace comma with period
                val temp = String.format(Locale.US, "%.1f", value).replace(",", ".")
                temp
            }
        } else {
            // USD: Use standard US formatting (comma for thousands, period for decimal)
            String.format(Locale.US, "%.2f", value)
        }
        
        return if (isVND) {
            "${formattedNumber}${suffix}₫"
        } else {
            "$${formattedNumber}${suffix}"
        }
    }
    
    /**
     * Get currency symbol
     */
    fun getCurrencySymbol(isVND: Boolean): String {
        return if (isVND) "₫" else "$"
    }
    
    /**
     * Get currency code
     */
    fun getCurrencyCode(isVND: Boolean): String {
        return if (isVND) "VND" else "USD"
    }
    
    /**
     * Validate amount input
     */
    fun isValidAmount(amountString: String): Boolean {
        val parsed = parseAmount(amountString)
        return parsed != null && parsed > 0
    }
    
    /**
     * Format amount for input field display
     */
    fun formatForInput(amount: Double, isVND: Boolean): String {
        return if (isVND) {
            // Format VND with periods as thousand separators and no decimal places
            val formatter = DecimalFormat("#,###").apply {
                decimalFormatSymbols = decimalFormatSymbols.apply {
                    groupingSeparator = '.'
                }
            }
            formatter.format(amount)
        } else {
            // Format USD with commas as thousand separators and 2 decimal places
            NumberFormat.getNumberInstance(Locale.US).apply {
                minimumFractionDigits = 2
                maximumFractionDigits = 2
            }.format(amount)
        }
    }

    fun calculatePercentage(saved: BigDecimal, target: BigDecimal): Float {
        return if (target > BigDecimal.ZERO) {
            (saved.toFloat() / target.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
    }
}
