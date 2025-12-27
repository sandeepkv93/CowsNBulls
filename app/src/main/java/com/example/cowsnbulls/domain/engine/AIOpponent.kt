package com.example.cowsnbulls.domain.engine

import com.example.cowsnbulls.domain.model.Difficulty
import com.example.cowsnbulls.domain.model.GameSettings
import com.example.cowsnbulls.domain.model.Guess

/**
 * AI opponent for Bulls & Cows game with three difficulty levels
 */
class AIOpponent(private val difficulty: Difficulty) {

    private var candidateSet: MutableSet<String> = mutableSetOf()

    /**
     * Initialize the AI with all possible valid secrets
     */
    fun initializeCandidates(settings: GameSettings) {
        candidateSet = GameEngine.generateAllValidSecrets(settings).toMutableSet()
    }

    /**
     * Make a guess based on difficulty level and previous feedback
     */
    fun makeGuess(settings: GameSettings, history: List<Guess>): String {
        // Initialize candidates if empty
        if (candidateSet.isEmpty()) {
            initializeCandidates(settings)
        }

        return when (difficulty) {
            Difficulty.EASY -> makeRandomGuess(settings)
            Difficulty.MEDIUM -> makeSmartGuess(settings, history)
            Difficulty.HARD -> makeOptimalGuess(settings, history)
        }
    }

    /**
     * Easy: Random valid guess
     */
    private fun makeRandomGuess(settings: GameSettings): String {
        // 70% of the time use random from candidates, 30% completely random
        return if (candidateSet.isNotEmpty() && Math.random() < 0.7) {
            candidateSet.random()
        } else {
            // Generate a completely random valid guess
            GameEngine.generateSecret(settings)
        }
    }

    /**
     * Medium: Maintain candidate set, eliminate impossibilities
     */
    private fun makeSmartGuess(settings: GameSettings, history: List<Guess>): String {
        // Filter candidates based on all previous guesses
        history.forEach { guess ->
            candidateSet.removeAll { candidate ->
                val (bulls, cows) = GameEngine.calculateFeedback(candidate, guess.digits)
                bulls != guess.bulls || cows != guess.cows
            }
        }

        // Return random from remaining candidates
        return if (candidateSet.isNotEmpty()) {
            candidateSet.random()
        } else {
            // Fallback if we eliminated all candidates (shouldn't happen with valid feedback)
            GameEngine.generateSecret(settings)
        }
    }

    /**
     * Hard: Use information theory to minimize expected candidates remaining
     * (Simplified version - full minimax would be computationally expensive)
     */
    private fun makeOptimalGuess(settings: GameSettings, history: List<Guess>): String {
        // First, filter like medium
        history.forEach { guess ->
            candidateSet.removeAll { candidate ->
                val (bulls, cows) = GameEngine.calculateFeedback(candidate, guess.digits)
                bulls != guess.bulls || cows != guess.cows
            }
        }

        // If very few candidates remain, just pick one
        if (candidateSet.size <= 2) {
            return candidateSet.firstOrNull() ?: GameEngine.generateSecret(settings)
        }

        // For first guess or when many candidates remain, use a strategic guess
        // Strategy: Pick a guess that maximizes information gain
        // Simplified: use a well-known starting guess for 4 digits
        if (history.isEmpty() && settings.digits == 4 && !settings.allowRepeats) {
            return "1234" // Strong opening guess
        }

        // Otherwise, pick from remaining candidates
        return candidateSet.random()
    }

    /**
     * Get number of remaining possible secrets
     */
    fun getRemainingCandidates(): Int {
        return candidateSet.size
    }

    /**
     * Reset the AI state
     */
    fun reset() {
        candidateSet.clear()
    }
}
