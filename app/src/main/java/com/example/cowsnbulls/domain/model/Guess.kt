package com.example.cowsnbulls.domain.model

/**
 * Represents a single guess with feedback
 */
data class Guess(
    val number: Int,                    // Guess attempt number (1, 2, 3, ...)
    val digits: String,                 // The guessed number as string (e.g., "1234")
    val bulls: Int,                     // Correct digits in correct position
    val cows: Int,                      // Correct digits in wrong position
    val timestamp: Long = System.currentTimeMillis()
)
