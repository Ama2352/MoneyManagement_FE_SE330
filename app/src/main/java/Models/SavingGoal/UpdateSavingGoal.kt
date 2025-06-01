package DI.Models.SavingGoal

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class UpdateSavingGoal(
    @SerializedName("savingGoalId") val savingGoalID: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("targetAmount") val targetAmount: BigDecimal,
    @SerializedName("description") val description: String,
    @SerializedName("categoryID") val categoryID: String,
    @SerializedName("walletID") val walletID: String
)