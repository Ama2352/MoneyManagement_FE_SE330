package DI.Models.Analysis

data class MonthlyTransactions(
    val month: String,
    val totalIncome: Double,
    val totalExpense: Double,
    val transactions: List<WeeklyTransactions>
)
