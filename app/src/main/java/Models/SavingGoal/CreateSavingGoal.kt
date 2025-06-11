package DI.Models.SavingGoal

import java.math.BigDecimal

data class CreateSavingGoal(
    val endDate: String,
    val startDate: String,
    val targetAmount: BigDecimal,
    val description: String,
    val categoryId: String, // Match server
    val walletId: String     // Match server
)