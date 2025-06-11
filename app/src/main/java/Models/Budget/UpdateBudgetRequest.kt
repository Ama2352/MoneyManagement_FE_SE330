package DI.Models.Budget

data class UpdateBudgetRequest(
    val endDate: String,
    val startDate: String,
    val limitAmount: Double,
    val description: String,
    val categoryId: String,
    val walletId: String,
    val budgetId: String
)