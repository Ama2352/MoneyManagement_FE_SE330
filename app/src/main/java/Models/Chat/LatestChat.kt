package DI.Models.Chat

typealias LatestChatResponses = Map<String, LatestChat>

data class LatestChat(
    val latestMessage: ChatMessage,
    val unreadCount: Int,

    val avatarUrl: String? = null
)
