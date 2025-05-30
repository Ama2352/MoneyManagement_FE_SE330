package DI.Models.Analysis

data class YearlyTransactions(
    val year: String,
    val totalIncome: Double,
    val totalExpense: Double,
    val transactions: List<MonthlyTransactions>
)
