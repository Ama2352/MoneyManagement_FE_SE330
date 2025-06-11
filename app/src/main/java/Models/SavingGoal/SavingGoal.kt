package DI.Models.SavingGoal

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

data class SavingGoal(
    @SerializedName("savingGoalId") val savingGoalID: String,
    @SerializedName("categoryId") val categoryID: String, // Match server response
    @SerializedName("walletId") val walletID: String,    // Match server response
    @SerializedName("description") val description: String,
    @SerializedName("targetAmount") val targetAmount: BigDecimal,
    @SerializedName("savedAmount") val savedAmount: BigDecimal,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("savedPercentage") val savedPercentage: BigDecimal = BigDecimal.ZERO,
    @SerializedName("progressStatus") val progressStatus: String = "Safe",
    @SerializedName("notification") val notification: String? = null
) {
    fun getCreatedAtAsLocalDateTime(): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return LocalDateTime.parse(createdAt, formatter)
    }

    fun getStartDateAsLocalDateTime(): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return LocalDateTime.parse(startDate, formatter)
    }

    fun getEndDateAsLocalDateTime(): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return LocalDateTime.parse(endDate, formatter)
    }
}