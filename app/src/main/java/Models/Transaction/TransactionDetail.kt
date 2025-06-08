package DI.Models.Transaction

data class TransactionDetail(
    val transactionID: String,
    val transactionDate: String,
    val date: String,
    val time: String = "",
    val dayOfWeek: String = "",
    val month: String = "",
    val amount: Double,
    val type: String = "",
    val category: String = "",
    val categoryID: String,
    val description: String,
    val walletID: String,
    val walletName: String = ""
)
