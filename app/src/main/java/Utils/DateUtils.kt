package DI.Utils

import android.content.Context
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import com.example.moneymanagement_frontend.R

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

    /**
     * Get localized days remaining text using string resources
     */
    fun getDaysRemainingText(context: Context, endDate: LocalDateTime): String {
        val days = getDaysRemaining(endDate)
        return when {
            days < 0 -> context.getString(R.string.date_expired)
            days == 0L -> context.getString(R.string.date_today)
            days == 1L -> context.getString(R.string.date_remaining_1_day)
            days <= 30 -> context.getString(R.string.date_remaining_days, days)
            else -> {
                val months = days / 30
                val remainingDays = days % 30
                if (remainingDays == 0L) {
                    context.getString(R.string.date_remaining_months, months)
                } else {
                    context.getString(R.string.date_remaining_months_days, months, remainingDays)
                }
            }
        }
    }
    
    fun isOverdue(endDate: LocalDateTime): Boolean {
        return LocalDateTime.now().isAfter(endDate)
    }
    
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
