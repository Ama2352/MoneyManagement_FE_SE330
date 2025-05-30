package DI.API

data class DailySummary (
    val dailyDetails: List<DailyDetail>,
    val totalIncome: Double,
    val totalExpense: Double,
)

data class DailyDetail (
    val dayOfWeek: String,
    val income: Double,
    val expense: Double
)

data class WeeklySummary (
    val weeklyDetails: List<WeeklyDetail>,
    val totalIncome: Double,
    val totalExpense: Double,
)

data class WeeklyDetail (
    val weekNumber: String, // ex: 1st, 2nd, 3rd, 4th
    val income: Double,
    val expense: Double
)

data class MonthlySummary (
    val monthlyDetails: List<MonthlyDetail>,
    val totalIncome: Double,
    val totalExpense: Double,
)

data class MonthlyDetail (
    val monthName: String,
    val income: Double,
    val expense: Double
)

data class YearlySummary (
    val yearlyDetails: List<YearlyDetail>,
    val totalIncome: Double,
    val totalExpense: Double,
)

data class YearlyDetail (
    val year: String,
    val income: Double,
    val expense: Double
)