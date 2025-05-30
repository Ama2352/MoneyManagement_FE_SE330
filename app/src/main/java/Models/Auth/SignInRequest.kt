package DI.Models.Auth

data class SignInRequest(
    val email: String,
    val password: String
)
