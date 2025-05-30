package DI.Models.Analysis.BarChart

data class YearlySummary(
    val yearlyDetails: List<YearlyDetails>,
    val totalIncome: Double,
    val totalExpenses: Double
)
