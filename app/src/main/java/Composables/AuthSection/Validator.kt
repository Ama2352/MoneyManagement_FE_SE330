package DI.Composables.AuthSection

data class ValidationResult(val isValid: Boolean, val errorMessage: String?)

object Validator {
    fun validateField(
        fieldType: String,
        value: String,
        confirmPassword: String? = null
    ): ValidationResult {
        return when (fieldType) {
            "firstName" -> {
                if (value.isBlank()) {
                    ValidationResult(false, "First name is required")
                } else {
                    ValidationResult(true, null)
                }
            }
            "lastName" -> {
                if (value.isBlank()) {
                    ValidationResult(false, "Last name is required")
                } else {
                    ValidationResult(true, null)
                }
            }
            "email" -> {
                if (value.isBlank()) {
                    ValidationResult(false, "Email is required")
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
                    ValidationResult(false, "Invalid email format")
                } else {
                    ValidationResult(true, null)
                }
            }
            "password" -> {
                if(value.isEmpty()) {
                    ValidationResult(false, "Password is required")
                } else if (value.length < 6) {
                    ValidationResult(false, "Password must be at least 6 characters")
                } else if (!value.any { it.isUpperCase() }) {
                    ValidationResult(false, "Password must contain at least one uppercase letter")
                } else if (!value.contains('@')) {
                    ValidationResult(false, "Password must contain @ symbol")
                } else {
                    ValidationResult(true, null)
                }
            }
            "confirmPassword" -> {
                if (confirmPassword.isNullOrEmpty()) {
                    ValidationResult(false, "Confirm password is required")
                } else if (value != confirmPassword) {
                    ValidationResult(false, "Password do not match")
                } else {
                    ValidationResult(true, null)
                }
            }
            else -> ValidationResult(true, null)
        }
    }
}