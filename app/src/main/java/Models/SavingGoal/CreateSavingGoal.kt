package DI.Models.SavingGoal

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class CreateSavingGoal(
    @SerializedName("endDate") val endDate: String,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("targetAmount") val targetAmount: BigDecimal,
    @SerializedName("description") val description: String,
    @SerializedName("categoryId") val categoryId: String, // Match server
    @SerializedName("walletId") val walletId: String     // Match server
)