package DI.Models.Transaction

data class CreateTransactionRequest(
    val categoryID: String,
    val amount: Double,
    val description: String = "No Description",
    val transactionDate: String,
    val walletID: String,
    val type: String // "Income" or "Expense"
)
