package DI.Models.Auth

data class RefreshTokenResponse(
    val success: Boolean,
    val message: String,
    val token: String
)