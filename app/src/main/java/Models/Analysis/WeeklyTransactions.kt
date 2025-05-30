package DI.Models.Analysis

data class WeeklyTransactions(
    val weekNumber: Int,
    val totalIncome: Double,
    val totalExpense: Double,
    val transactions: List<TransactionByCalendar>
)
