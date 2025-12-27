package com.example.cowsnbulls.domain.engine

import com.example.cowsnbulls.domain.model.GameSettings

/**
 * Core game engine for Bulls & Cows
 * Pure functions with no Android dependencies for easy testing
 */
object GameEngine {

    /**
     * Calculate bulls and cows for a guess against a secret
     * Bulls: correct digit in correct position
     * Cows: correct digit in wrong position
     *
     * @param secret The secret code to match against
     * @param guess The player's guess
     * @return Pair of (bulls, cows)
     */
    fun calculateFeedback(secret: String, guess: String): Pair<Int, Int> {
        require(secret.length == guess.length) { "Secret and guess must be same length" }

        var bulls = 0
        var cows = 0

        val secretChars = secret.toCharArray()
        val guessChars = guess.toCharArray()
        val matched = BooleanArray(secret.length) { false }

        // First pass: count bulls (exact matches)
        for (i in secretChars.indices) {
            if (secretChars[i] == guessChars[i]) {
                bulls++
                matched[i] = true
                guessChars[i] = 'X' // Mark as matched
            }
        }

        // Second pass: count cows (correct digit, wrong position)
        for (i in guessChars.indices) {
            if (guessChars[i] != 'X') { // Not already matched as bull
                for (j in secretChars.indices) {
                    if (!matched[j] && secretChars[j] == guessChars[i]) {
                        cows++
                        matched[j] = true
                        break
                    }
                }
            }
        }

        return Pair(bulls, cows)
    }

    /**
     * Generate a random valid secret based on settings
     *
     * @param settings Game configuration
     * @return Valid secret code as string
     */
    fun generateSecret(settings: GameSettings): String {
        val availableDigits = (0..9).toMutableList()

        return if (settings.allowRepeats) {
            // Can repeat digits - just generate random
            buildString {
                repeat(settings.digits) {
                    append(availableDigits.random())
                }
            }.let { secret ->
                // Ensure no leading zero if disabled
                if (!settings.allowLeadingZero && secret.startsWith("0")) {
                    // Swap first digit with a non-zero digit
                    val nonZeroDigits = secret.filter { it != '0' }
                    if (nonZeroDigits.isNotEmpty()) {
                        nonZeroDigits.first() + secret.substring(1)
                    } else {
                        // Edge case: all zeros, regenerate
                        generateSecret(settings)
                    }
                } else {
                    secret
                }
            }
        } else {
            // No repeats - shuffle and take first N
            availableDigits.shuffle()
            val secret = availableDigits.take(settings.digits).joinToString("")

            // Ensure no leading zero if disabled
            if (!settings.allowLeadingZero && secret.startsWith("0")) {
                // Find first non-zero digit and swap
                val nonZeroIndex = secret.indexOfFirst { it != '0' }
                if (nonZeroIndex != -1) {
                    val chars = secret.toCharArray()
                    chars[0] = chars[nonZeroIndex].also { chars[nonZeroIndex] = chars[0] }
                    chars.joinToString("")
                } else {
                    // All zeros (impossible with unique digits), regenerate
                    generateSecret(settings)
                }
            } else {
                secret
            }
        }
    }

    /**
     * Validate a guess against game settings
     *
     * @param guess The player's guess
     * @param settings Game configuration
     * @return ValidationResult indicating if guess is valid
     */
    fun validateGuess(guess: String, settings: GameSettings): ValidationResult {
        // Check length
        if (guess.length != settings.digits) {
            return ValidationResult.InvalidLength
        }

        // Check all characters are digits
        if (!guess.all { it.isDigit() }) {
            return ValidationResult.NonNumeric
        }

        // Check for repeated digits if not allowed
        if (!settings.allowRepeats && guess.toSet().size != guess.length) {
            return ValidationResult.RepeatedDigits
        }

        // Check for leading zero if not allowed
        if (!settings.allowLeadingZero && guess.startsWith("0")) {
            return ValidationResult.LeadingZero
        }

        return ValidationResult.Valid
    }

    /**
     * Check if game is won (all bulls, no cows)
     *
     * @param bulls Number of bulls
     * @param digits Total number of digits in secret
     * @return True if player won
     */
    fun isWon(bulls: Int, digits: Int): Boolean {
        return bulls == digits
    }

    /**
     * Generate all possible valid secrets for a given settings
     * Used by AI opponent for candidate set generation
     *
     * @param settings Game configuration
     * @return List of all valid secret codes
     */
    fun generateAllValidSecrets(settings: GameSettings): List<String> {
        val secrets = mutableListOf<String>()

        fun backtrack(current: String, usedDigits: Set<Char>) {
            if (current.length == settings.digits) {
                if (validateGuess(current, settings) == ValidationResult.Valid) {
                    secrets.add(current)
                }
                return
            }

            for (digit in '0'..'9') {
                // Skip if digit already used and repeats not allowed
                if (!settings.allowRepeats && digit in usedDigits) continue

                // Skip leading zero if not allowed
                if (current.isEmpty() && !settings.allowLeadingZero && digit == '0') continue

                backtrack(current + digit, usedDigits + digit)
            }
        }

        backtrack("", emptySet())
        return secrets
    }
}
