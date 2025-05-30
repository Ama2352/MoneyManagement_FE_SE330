package DI.Models.Chat

data class Chat(
    val chatId: String,
    val otherUserId: String,
    val otherUserName: String,
    val messages: List<ChatMessage>, // Trả về all chats thì ko cần trả lịch sử tin nhắn -> Bỏ được

    val avatarUrl: String? = null
)
