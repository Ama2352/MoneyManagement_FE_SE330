package DI.Utils

import androidx.compose.ui.graphics.Color

object TransactionUtils {
    /**
     * Get color based on transaction type
     */
    fun getTransactionColor(type: String): Color {
        return when (type.lowercase()) {
            "income" -> Color(0xFF059669) // Green for income
            "expense" -> Color(0xFFDC2626) // Red for expense
            else -> Color(0xFF6B7280) // Gray for other types
        }
    }
}
