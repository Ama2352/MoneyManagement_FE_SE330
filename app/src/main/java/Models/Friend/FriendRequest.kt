package DI.Models.Friend

data class FriendRequest(
    val userId: String,
    val username: String,
    val displayName: String,
    val requestedAt: String
)
