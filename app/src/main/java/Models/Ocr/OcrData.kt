package DI.Models.Ocr

data class OcrData (
    val transactionId: String,
    val amount: Double,
    val date: String,
    val bankName: String
)