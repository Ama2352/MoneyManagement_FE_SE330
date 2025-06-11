package DI.Composables.AuthSection

data class ValidationResult(val isValid: Boolean, val errorMessage: String?)

data class ValidationStrings(
    val firstNameRequired: String,
    val lastNameRequired: String,
    val emailRequired: String,
    val emailInvalid: String,
    val passwordRequired: String,
    val passwordMinLength: String,
    val passwordUppercase: String,
    val passwordSymbol: String,
    val confirmPasswordRequired: String,
    val passwordsNoMatch: String
)

object Validator {
    fun validateField(
        fieldType: String,
        value: String,
        confirmPassword: String? = null,
        validationStrings: ValidationStrings
    ): ValidationResult {
        return when (fieldType) {
            "firstName" -> {
                if (value.isBlank()) {
                    ValidationResult(false, validationStrings.firstNameRequired)
                } else {
                    ValidationResult(true, null)
                }
            }
            "lastName" -> {
                if (value.isBlank()) {
                    ValidationResult(false, validationStrings.lastNameRequired)
                } else {
                    ValidationResult(true, null)
                }
            }
            "email" -> {
                if (value.isBlank()) {
                    ValidationResult(false, validationStrings.emailRequired)
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
                    ValidationResult(false, validationStrings.emailInvalid)
                } else {
                    ValidationResult(true, null)
                }
            }
            "password" -> {
                if(value.isEmpty()) {
                    ValidationResult(false, validationStrings.passwordRequired)
                } else if (value.length < 6) {
                    ValidationResult(false, validationStrings.passwordMinLength)
                } else if (!value.any { it.isUpperCase() }) {
                    ValidationResult(false, validationStrings.passwordUppercase)
                } else if (!value.contains('@')) {
                    ValidationResult(false, validationStrings.passwordSymbol)
                } else {
                    ValidationResult(true, null)
                }
            }
            "confirmPassword" -> {
                if (confirmPassword.isNullOrEmpty()) {
                    ValidationResult(false, validationStrings.confirmPasswordRequired)
                } else if (value != confirmPassword) {
                    ValidationResult(false, validationStrings.passwordsNoMatch)
                } else {
                    ValidationResult(true, null)
                }
            }
            else -> ValidationResult(true, null)
        }
    }
}