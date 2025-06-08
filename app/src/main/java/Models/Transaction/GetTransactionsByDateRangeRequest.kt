package DI.Models.Transaction

data class GetTransactionsByDateRangeRequest(
    val startDate: String? = null,
    val endDate: String? = null,
    val type: String? = null,
    val category: String? = null,
    val timeRange: String? = null,
    val dayOfWeek: String? = null
)
