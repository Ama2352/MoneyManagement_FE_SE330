package DI.Models.UserInfo

data class  UpdatedProfile(
    val firstName: String?,
    val lastName: String?,
    val currentPassword: String,
    val newPassword: String?,
    val confirmNewPassword: String?
)
