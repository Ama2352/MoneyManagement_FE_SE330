package DI.Models.SavingGoal

import java.math.BigDecimal

data class UpdateSavingGoal(
    val savingGoalId: String,
    val endDate: String,
    val startDate: String,
    val targetAmount: BigDecimal,
    val description: String,
    val categoryId: String,
    val walletId: String
)