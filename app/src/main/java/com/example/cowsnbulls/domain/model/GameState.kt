package com.example.cowsnbulls.domain.model

/**
 * Complete state of a Bulls & Cows game session
 */
data class GameState(
    val mode: GameMode = GameMode.VS_COMPUTER,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val settings: GameSettings = GameSettings(),
    val secret: String = "",
    val currentGuess: String = "",
    val guessHistory: List<Guess> = emptyList(),
    val usedDigits: Set<String> = emptySet(),
    val elapsedTime: Long = 0,
    val isWon: Boolean = false,
    val opponent: Opponent = Opponent.AI,
    val isMyTurn: Boolean = true  // For multiplayer mode
) {
    /**
     * Check if current guess is complete
     */
    val isGuessComplete: Boolean
        get() = currentGuess.length == settings.digits

    /**
     * Get the next guess number
     */
    val nextGuessNumber: Int
        get() = guessHistory.size + 1
}
