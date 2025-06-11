package DI.Utils

import DI.Models.Currency.CurrencyRates
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * Budget Notification Translator
 * 
 * Handles translation and currency formatting for budget notifications
 * based on app's current language and currency settings
 */
object BudgetNotificationTranslator {
    
    /**
     * Main function to translate and format notification messages
     * Uses app's current locale and currency preference
     */
    @Composable
    fun translate(
        message: String,
        isVND: Boolean,
        exchangeRates: CurrencyRates?
    ): String {
        val context = LocalContext.current
        val isVietnamese = isAppInVietnamese(context)
        
        // Step 1: Translate the message based on current language
        val translatedMessage = if (isVietnamese) {
            translateToVietnamese(message)
        } else {
            message // Keep English
        }
        
        // Step 2: Convert currency amounts in the message
        return convertCurrencyInMessage(translatedMessage, isVND, exchangeRates)
    }
    
    /**
     * Check if app is currently in Vietnamese
     */
    private fun isAppInVietnamese(context: Context): Boolean {
        return context.resources.configuration.locale.language == "vi"
    }
    
    /**
     * Translate English notification messages to Vietnamese
     */
    private fun translateToVietnamese(message: String): String {
        // If already contains Vietnamese characters, return as is
        if (containsVietnamese(message)) {
            return message
        }
        
        // Pattern-based translation
        return when {
            // Budget spending patterns
            message.contains("You have spent") && message.contains("of your budget") -> {
                val amounts = extractAmounts(message)
                val percentage = extractPercentage(message)
                
                when {
                    amounts.size >= 2 && percentage != null -> 
                        "💰 Bạn đã chi tiêu ${amounts[0]} trong tổng ngân sách ${amounts[1]} (${percentage}%)"
                    amounts.size >= 2 -> 
                        "💰 Bạn đã chi tiêu ${amounts[0]} trong tổng ngân sách ${amounts[1]}"
                    percentage != null ->
                        "📊 Bạn đã sử dụng ${percentage}% ngân sách"
                    else -> "💰 Bạn đã chi tiêu một phần ngân sách"
                }
            }
            
            // Budget exceeded
            message.contains("Budget limit exceeded") || message.contains("over budget") -> {
                val amounts = extractAmounts(message)
                when {
                    amounts.size >= 2 -> "🚨 Vượt giới hạn! Chi tiêu: ${amounts[0]}, Hạn mức: ${amounts[1]}"
                    amounts.size == 1 -> "🚨 Vượt giới hạn ngân sách: ${amounts[0]}"
                    else -> "🚨 Đã vượt quá giới hạn ngân sách"
                }
            }
            
            // Status messages
            message.contains("on track") -> "✅ Đúng kế hoạch - Chi tiêu hợp lý"
            message.contains("Under budget") -> "💚 Dưới ngân sách - Tiết kiệm tốt!"
            message.contains("Minimal spending") -> "💎 Chi tiêu tối thiểu"
            message.contains("not started") -> "🕐 Ngân sách chưa bắt đầu"
            message.contains("Budget completed") -> "✅ Ngân sách đã hoàn thành"
            
            // Warning levels
            message.contains("Warning") && message.contains("% of budget") -> {
                val percentage = extractPercentage(message)
                if (percentage != null) {
                    "⚠️ Cảnh báo: Đã dùng ${percentage}% ngân sách"
                } else {
                    "⚠️ Cảnh báo: Chi tiêu cao"
                }
            }
            
            message.contains("Critical") && message.contains("% of budget") -> {
                val percentage = extractPercentage(message)
                if (percentage != null) {
                    "🚨 Nguy hiểm: Đã dùng ${percentage}% ngân sách"
                } else {
                    "🚨 Mức chi tiêu nguy hiểm"
                }
            }
            
            // Nearly maxed
            message.contains("Nearly maxed") || message.contains("nearly exceeded") -> {
                val amounts = extractAmounts(message)
                if (amounts.size >= 2) {
                    "⚡ Gần hết hạn mức: ${amounts[0]} / ${amounts[1]}"
                } else {
                    "⚡ Gần đạt giới hạn ngân sách"
                }
            }
            
            // Time warnings
            message.contains("days left") || message.contains("day left") -> {
                val days = extractDays(message)
                if (days != null) {
                    when {
                        days <= 1 -> "⏰ Còn $days ngày - Sắp hết hạn!"
                        days <= 3 -> "⏳ Còn $days ngày - Gần hết hạn"
                        days <= 7 -> "📅 Còn $days ngày"
                        else -> "📆 Còn $days ngày"
                    }
                } else message
            }
            
            // Percentage patterns
            message.matches(Regex(""".*(\d+)%.*used.*""")) -> {
                val percentage = extractPercentage(message)
                if (percentage != null) {
                    when {
                        percentage >= 95 -> "🔥 Đã dùng ${percentage}% - Gần cạn kiệt!"
                        percentage >= 90 -> "🚨 Đã dùng ${percentage}% - Nguy hiểm!"
                        percentage >= 80 -> "⚠️ Đã dùng ${percentage}% - Cần cẩn trọng"
                        percentage >= 70 -> "📊 Đã dùng ${percentage}% - Chú ý"
                        percentage >= 50 -> "📈 Đã dùng ${percentage}% - Tốt"
                        else -> "💚 Đã dùng ${percentage}% - Còn nhiều"
                    }
                } else message
            }
            
            // Simple amount messages
            message.matches(Regex("""\$[0-9,]+\.?[0-9]*""")) -> "💰 Chi tiêu: $message"
            
            // Default
            else -> message
        }
    }
    
    /**
     * Convert currency amounts in messages from VND (backend) to user's preferred currency
     */
    private fun convertCurrencyInMessage(
        message: String,
        isVND: Boolean,
        exchangeRates: CurrencyRates?
    ): String {
        // Patterns for different currency formats
        val patterns = listOf(
            // USD format: $123.45, $1,234.56
            Regex("""\$([0-9,]+\.?[0-9]*)""") to false,
            // VND with symbol: 123₫, 1,234₫
            Regex("""([0-9,]+\.?[0-9]*)[₫đ]""") to true,
            // VND with word: 123 VND, 1,234 VND
            Regex("""([0-9,]+\.?[0-9]*)\s*VND""") to true,
            // Large numbers (assume VND): 123456, 1,234,567
            Regex("""\b([0-9]{4,}(?:,[0-9]{3})*)\b""") to true
        )
        
        var result = message
        
        patterns.forEach { (regex, isSourceVND) ->
            result = regex.replace(result) { matchResult ->
                val amountStr = matchResult.groupValues[1].replace(",", "")
                val amount = amountStr.toDoubleOrNull() ?: return@replace matchResult.value
                
                // Convert currency if needed
                val convertedAmount = when {
                    isSourceVND && !isVND -> {
                        // Convert VND to USD
                        val rate = exchangeRates?.usdToVnd ?: 24000.0
                        CurrencyUtils.vndToUsd(amount, rate)
                    }
                    !isSourceVND && isVND -> {
                        // Convert USD to VND
                        val rate = exchangeRates?.usdToVnd ?: 24000.0
                        CurrencyUtils.usdToVnd(amount, rate)
                    }
                    else -> amount // Same currency
                }
                
                // Format in target currency
                CurrencyUtils.formatAmount(convertedAmount, isVND)
            }
        }
        
        return result
    }
    
    /**
     * Extract currency amounts from message
     */
    private fun extractAmounts(message: String): List<String> {
        val regex = Regex("""\$([0-9,]+\.?[0-9]*)|([0-9,]+\.?[0-9]*)[₫đ]|([0-9,]+\.?[0-9]*)\s*VND""")
        return regex.findAll(message).map { match ->
            match.groupValues.find { it.isNotEmpty() && it != match.value } ?: match.value
        }.toList()
    }
    
    /**
     * Extract percentage from message
     */
    private fun extractPercentage(message: String): Int? {
        val regex = Regex("""(\d+)%""")
        return regex.find(message)?.groupValues?.get(1)?.toIntOrNull()
    }
    
    /**
     * Extract days from message
     */
    private fun extractDays(message: String): Int? {
        val regex = Regex("""(\d+)\s*days?\s*left""")
        return regex.find(message)?.groupValues?.get(1)?.toIntOrNull()
    }
    
    /**
     * Check if text contains Vietnamese characters
     */
    private fun containsVietnamese(text: String): Boolean {
        val vietnameseChars = setOf(
            'à', 'á', 'ạ', 'ả', 'ã', 'â', 'ầ', 'ấ', 'ậ', 'ẩ', 'ẫ', 'ă', 'ằ', 'ắ', 'ặ', 'ẳ', 'ẵ',
            'è', 'é', 'ẹ', 'ẻ', 'ẽ', 'ê', 'ề', 'ế', 'ệ', 'ể', 'ễ',
            'ì', 'í', 'ị', 'ỉ', 'ĩ',
            'ò', 'ó', 'ọ', 'ỏ', 'õ', 'ô', 'ồ', 'ố', 'ộ', 'ổ', 'ỗ', 'ơ', 'ờ', 'ớ', 'ợ', 'ở', 'ỡ',
            'ù', 'ú', 'ụ', 'ủ', 'ũ', 'ư', 'ừ', 'ứ', 'ự', 'ử', 'ữ',
            'ỳ', 'ý', 'ỵ', 'ỷ', 'ỹ', 'đ'
        )
        return text.any { it.lowercaseChar() in vietnameseChars }
    }
}
