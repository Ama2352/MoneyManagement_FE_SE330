package DI.Utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateUtils {
    private val displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    fun formatDateForDisplay(dateTime: LocalDateTime): String {
        return dateTime.format(displayFormatter)
    }
    
    fun formatDateForApi(dateTime: LocalDateTime): String {
        return dateTime.format(apiFormatter)
    }
    
    fun parseApiDate(dateString: String): LocalDateTime {
        return LocalDateTime.parse(dateString, apiFormatter)
    }
    
    fun getDaysRemaining(endDate: LocalDateTime): Long {
        val now = LocalDateTime.now()
        return ChronoUnit.DAYS.between(now, endDate)
    }
    
    // Updated function that uses string resources
    fun getDaysRemainingText(endDate: LocalDateTime, strings: AppStrings): String {
        val days = getDaysRemaining(endDate)
        return when {
            days < 0 -> strings.dateExpired
            days == 0L -> strings.dateToday
            days == 1L -> strings.dateOneDayLeft
            days <= 30 -> strings.dateDaysLeft.format(days)
            else -> {
                val months = days / 30
                val remainingDays = days % 30
                if (remainingDays == 0L) {
                    strings.dateMonthsLeft.format(months)
                } else {
                    strings.dateMonthsDaysLeft.format(months, remainingDays)
                }
            }
        }
    }
    
    // Legacy function for backward compatibility (deprecated)
    @Deprecated("Use getDaysRemainingText(endDate, strings) instead")
    fun getDaysRemainingText(endDate: LocalDateTime): String {
        val days = getDaysRemaining(endDate)
        return when {
            days < 0 -> "Đã hết hạn"
            days == 0L -> "Hôm nay"
            days == 1L -> "Còn 1 ngày"
            days <= 30 -> "Còn $days ngày"
            else -> {
                val months = days / 30
                val remainingDays = days % 30
                if (remainingDays == 0L) {
                    "Còn $months tháng"
                } else {
                    "Còn $months tháng $remainingDays ngày"
                }
            }
        }
    }
    
    fun isOverdue(endDate: LocalDateTime): Boolean {
        return LocalDateTime.now().isAfter(endDate)
    }
    
    // Updated function that uses string resources
    fun getProgressStatus(savedPercentage: Float, daysRemaining: Long, strings: AppStrings): String {
        return when {
            daysRemaining < 0 -> strings.dateStatusOverdue
            savedPercentage >= 1.0f -> strings.dateStatusCompleted
            daysRemaining <= 7 && savedPercentage < 0.8f -> strings.dateStatusUrgent
            daysRemaining <= 30 && savedPercentage < 0.5f -> strings.dateStatusWarning
            else -> strings.dateStatusOnTrack
        }
    }
    
    // Legacy function for backward compatibility (deprecated)
    @Deprecated("Use getProgressStatus(savedPercentage, daysRemaining, strings) instead")
    fun getProgressStatus(savedPercentage: Float, daysRemaining: Long): String {
        return when {
            daysRemaining < 0 -> "Overdue"
            savedPercentage >= 1.0f -> "Completed"
            daysRemaining <= 7 && savedPercentage < 0.8f -> "Urgent"
            daysRemaining <= 30 && savedPercentage < 0.5f -> "Warning"
            else -> "On Track"
        }
    }
}
