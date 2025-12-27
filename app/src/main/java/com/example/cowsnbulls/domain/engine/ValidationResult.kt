package com.example.cowsnbulls.domain.engine

/**
 * Result of validating a guess against game settings
 */
sealed class ValidationResult {
    object Valid : ValidationResult()
    object InvalidLength : ValidationResult()
    object RepeatedDigits : ValidationResult()
    object LeadingZero : ValidationResult()
    object NonNumeric : ValidationResult()

    /**
     * Check if validation passed
     */
    val isValid: Boolean
        get() = this is Valid

    /**
     * Get user-friendly error message
     */
    fun getErrorMessage(): String? = when (this) {
        is Valid -> null
        is InvalidLength -> "Guess must have the correct number of digits"
        is RepeatedDigits -> "Digits must be unique (no repeats allowed)"
        is LeadingZero -> "First digit cannot be 0"
        is NonNumeric -> "Guess must contain only digits"
    }
}
