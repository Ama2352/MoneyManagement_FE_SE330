package DI.Models.UserInfo

data class  UpdatedProfile(
    val FirstName: String?,
    val LastName: String?,
    val CurrentPassword: String,
    val NewPassword: String?,
    val ConfirmNewPassword: String?
)
