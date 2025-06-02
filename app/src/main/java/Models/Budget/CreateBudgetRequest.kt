package DI.Models.Budget

data class CreateBudgetRequest(
    val endDate: String,
    val startDate: String,
    val limitAmount: Double,
    val description: String,
    val categoryID: String,
    val walletID: String
)