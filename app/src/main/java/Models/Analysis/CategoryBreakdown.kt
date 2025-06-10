package DI.Models.Analysis

data class CategoryBreakdown(
    val category: String,
    val totalIncome: Double,
    val totalExpense: Double,
    val incomePercentage: Double,
    val expensePercentage: Double,
)