package DI.Models.Analysis

data class CategoryBreakdown(
    val category: String,
    val totalIncome: Double,
    val totalExpenses: Double,
    val incomePercentage: Double,
    val expensePercentage: Double,
)