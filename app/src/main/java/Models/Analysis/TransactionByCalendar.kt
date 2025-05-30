package DI.Models.Analysis

data class TransactionByCalendar(
    val description: String,
    val date: String,
    val time: String,
    val dayOfWeek: String,
    val amount: Double,
    val type: String,
    val category: String
)
