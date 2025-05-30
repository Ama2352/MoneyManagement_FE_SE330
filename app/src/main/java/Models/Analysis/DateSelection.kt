package DI.Models.Analysis

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DateSelection(
    val start: LocalDate? = null,
    val end: LocalDate? = null
) {
    fun isDateInRange(date: LocalDate): Boolean {
        val startDate = start ?: return false
        val endDate = end ?: return false
        return date.isAfter(startDate) && date.isBefore(endDate)
    }

    fun getSelectionAsYearMonthRange(): String {
        if(start == null || end == null || start.isBefore(end)) return "Jan 2024 - Dec 2025"
        val startDate = formatDate(start)
        val endDate = formatDate(end)
        return "$startDate - $endDate"
    }

    fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date.format(formatter)
    }
}
