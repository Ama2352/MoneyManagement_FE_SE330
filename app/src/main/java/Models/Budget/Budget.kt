package DI.Models.Budget

data class Budget(
    val budgetId: String,
    val categoryId: String,
    val walletId: String,
    val description: String,
    val limitAmount: Double,
    val currentSpending: Double,
    val startDate: String,
    val endDate: String,
    val createdAt: String,
    val usagePercentage: Double,
    val progressStatus: String,
    val notification: String?
)