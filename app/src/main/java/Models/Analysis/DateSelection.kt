package DI.Models.Analysis

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class DateSelection(val start: LocalDate? = null, val end: LocalDate? = null) {
    fun isDateInRange(date: LocalDate): Boolean {
        val startDate = start ?: return false
        val endDate = end ?: return false
        return date.isAfter(startDate) && date.isBefore(endDate)
    }

    fun getSelectionAsYearMonthRange(): String {
        if (start == null || end == null || start.isAfter(end)) return "Select date range"
        val startDate = formatDateForDisplay(start)
        val endDate = formatDateForDisplay(end)
        return "$startDate - $endDate"
    }

    fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date.format(formatter)
    }

    private fun formatDateForDisplay(date: LocalDate): String {
        val currentLocale = Locale.getDefault()

        return when (currentLocale.language) {
            "vi" -> {
                // Vietnamese format: dd MMM yyyy (day first)
                val formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy", currentLocale)
                date.format(formatter)
            }

            else -> {
                // English and other locales: MMM dd, yyyy (month first)
                val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", currentLocale)
                date.format(formatter)
            }
        }
    }
}
