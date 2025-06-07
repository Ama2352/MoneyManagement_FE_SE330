package DI.Models.Transaction

data class Transaction(
    val transactionID: String,
    val walletID: String,
    val categoryID: String,
    val amount: Double,
    val description: String,
    val type: String,
    val transactionDate: String
)