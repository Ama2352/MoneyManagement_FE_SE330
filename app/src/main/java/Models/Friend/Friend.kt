package DI.Models.Friend

data class Friend(
    val userId: String,
    val username: String,
    val displayName: String,
    val isOnline: Boolean,
    val lastActive: String,
    val isPendingRequest: Boolean
)
