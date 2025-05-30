package DI.Models.Analysis.BarChart

data class WeeklySummary(
    val weeklyDetails: List<WeeklyDetails>,
    val totalIncome: Double,
    val totalExpenses: Double
)