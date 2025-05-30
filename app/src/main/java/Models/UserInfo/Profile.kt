package DI.Models.UserInfo

data class Profile (
    val id: String,
    val email: String,
    val userName: String,
    val firstName: String,
    val lastName: String,
    val displayName: String,
    val avatarUrl: String,
)
