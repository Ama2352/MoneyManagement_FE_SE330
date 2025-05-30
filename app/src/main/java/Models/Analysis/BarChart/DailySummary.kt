package DI.Models.Analysis.BarChart

data class DailySummary(
    val dailyDetails: List<DailyDetails>,
    val totalIncome: Double,
    val totalExpenses: Double
)
