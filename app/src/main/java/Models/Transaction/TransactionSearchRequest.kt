package DI.Models.Transaction

data class TransactionSearchRequest(
    val startDate: String? = null,
    val endDate: String? = null,
    val type: String? = null,
    val category: String? = null,
    val amountRange: String? = null,
    val keywords: String? = null,
    val timeRange: String? = null,
    val dayOfWeek: String? = null
)