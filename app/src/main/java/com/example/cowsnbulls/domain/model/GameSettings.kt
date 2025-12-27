package com.example.cowsnbulls.domain.model

/**
 * Configuration settings for a Bulls & Cows game
 */
data class GameSettings(
    val digits: Int = 4,
    val allowRepeats: Boolean = false,
    val allowLeadingZero: Boolean = false
) {
    init {
        require(digits in 3..6) { "Digits must be between 3 and 6" }
    }
}

/**
 * AI difficulty levels for VS Computer mode
 */
enum class Difficulty {
    EASY,    // Random valid guesses
    MEDIUM,  // Maintains candidate set, eliminates impossibilities
    HARD     // Uses information theory (entropy-based guessing)
}

/**
 * Game mode selection
 */
enum class GameMode {
    VS_COMPUTER,
    MULTIPLAYER
}

/**
 * Opponent representation in the game
 */
sealed class Opponent {
    object AI : Opponent()
    data class Player(val name: String, val avatar: String = "👤") : Opponent()
}
