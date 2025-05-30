package DI.Models.Auth

data class RefreshTokenRequest(
    val expiredToken: String
)
