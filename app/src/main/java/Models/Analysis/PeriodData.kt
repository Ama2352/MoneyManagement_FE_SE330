package DI.Models.Analysis

data class PeriodData(
    val labels: List<String>,
    val income: List<Double>,
    val expenses: List<Double>,
    val totalIncome: Double,
    val totalExpenses: Double
)
