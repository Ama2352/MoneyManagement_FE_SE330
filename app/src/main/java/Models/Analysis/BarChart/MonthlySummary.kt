package DI.Models.Analysis.BarChart

data class MonthlySummary(
    val monthlyDetails: List<MonthlyDetails>,
    val totalIncome: Double,
    val totalExpenses: Double
)
