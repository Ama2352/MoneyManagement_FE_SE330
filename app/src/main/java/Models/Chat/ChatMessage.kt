package DI.Models.Chat

data class ChatMessage(
    val messageID: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val sentAt: String, // Consider using `LocalDateTime` with a converter if you need it parsed
    val senderName: String,
    val receiverName: String
)
