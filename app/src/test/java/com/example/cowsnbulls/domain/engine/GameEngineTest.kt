package com.example.cowsnbulls.domain.engine

import com.example.cowsnbulls.domain.model.GameSettings
import org.junit.Assert.*
import org.junit.Test

class GameEngineTest {

    @Test
    fun `calculateFeedback returns correct bulls and cows`() {
        val secret = "1234"
        val guess = "1243"
        val (bulls, cows) = GameEngine.calculateFeedback(secret, guess)

        assertEquals(2, bulls) // 1 and 2 in correct positions
        assertEquals(2, cows)  // 4 and 3 in wrong positions
    }

    @Test
    fun `calculateFeedback all bulls returns correct result`() {
        val secret = "5678"
        val guess = "5678"
        val (bulls, cows) = GameEngine.calculateFeedback(secret, guess)

        assertEquals(4, bulls)
        assertEquals(0, cows)
    }

    @Test
    fun `calculateFeedback all wrong returns zero bulls and cows`() {
        val secret = "1234"
        val guess = "5678"
        val (bulls, cows) = GameEngine.calculateFeedback(secret, guess)

        assertEquals(0, bulls)
        assertEquals(0, cows)
    }

    @Test
    fun `calculateFeedback all cows returns correct result`() {
        val secret = "1234"
        val guess = "4321"
        val (bulls, cows) = GameEngine.calculateFeedback(secret, guess)

        assertEquals(0, bulls)
        assertEquals(4, cows)
    }

    @Test
    fun `calculateFeedback partial match`() {
        val secret = "1234"
        val guess = "1567"
        val (bulls, cows) = GameEngine.calculateFeedback(secret, guess)

        assertEquals(1, bulls) // 1 in correct position
        assertEquals(0, cows)
    }

    @Test
    fun `calculateFeedback with repeated digits in guess`() {
        val secret = "1234"
        val guess = "1123"
        val (bulls, cows) = GameEngine.calculateFeedback(secret, guess)

        assertEquals(1, bulls) // Only first 1 matches
        assertEquals(2, cows)  // 2 and 3 are in secret but wrong positions
    }

    @Test
    fun `generateSecret creates valid secret with default settings`() {
        val settings = GameSettings(digits = 4, allowRepeats = false, allowLeadingZero = false)
        val secret = GameEngine.generateSecret(settings)

        assertEquals(4, secret.length)
        assertEquals(4, secret.toSet().size) // All unique
        assertNotEquals('0', secret[0]) // No leading zero
        assertTrue(secret.all { it.isDigit() })
    }

    @Test
    fun `generateSecret creates secret with repeats when allowed`() {
        val settings = GameSettings(digits = 4, allowRepeats = true, allowLeadingZero = false)
        val secret = GameEngine.generateSecret(settings)

        assertEquals(4, secret.length)
        assertNotEquals('0', secret[0]) // No leading zero
        assertTrue(secret.all { it.isDigit() })
    }

    @Test
    fun `generateSecret creates secret with leading zero when allowed`() {
        val settings = GameSettings(digits = 4, allowRepeats = false, allowLeadingZero = true)

        // Generate multiple secrets to increase chance of getting one with leading zero
        val secrets = (1..20).map { GameEngine.generateSecret(settings) }

        // At least check they're all valid
        secrets.forEach { secret ->
            assertEquals(4, secret.length)
            assertEquals(4, secret.toSet().size)
            assertTrue(secret.all { it.isDigit() })
        }
    }

    @Test
    fun `validateGuess detects invalid length`() {
        val settings = GameSettings(digits = 4)
        val result = GameEngine.validateGuess("123", settings)

        assertEquals(ValidationResult.InvalidLength, result)
    }

    @Test
    fun `validateGuess detects repeated digits`() {
        val settings = GameSettings(digits = 4, allowRepeats = false)
        val result = GameEngine.validateGuess("1123", settings)

        assertEquals(ValidationResult.RepeatedDigits, result)
    }

    @Test
    fun `validateGuess allows repeated digits when enabled`() {
        val settings = GameSettings(digits = 4, allowRepeats = true)
        val result = GameEngine.validateGuess("1123", settings)

        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `validateGuess detects leading zero`() {
        val settings = GameSettings(digits = 4, allowLeadingZero = false)
        val result = GameEngine.validateGuess("0123", settings)

        assertEquals(ValidationResult.LeadingZero, result)
    }

    @Test
    fun `validateGuess allows leading zero when enabled`() {
        val settings = GameSettings(digits = 4, allowLeadingZero = true, allowRepeats = false)
        val result = GameEngine.validateGuess("0123", settings)

        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `validateGuess detects non-numeric input`() {
        val settings = GameSettings(digits = 4)
        val result = GameEngine.validateGuess("12a4", settings)

        assertEquals(ValidationResult.NonNumeric, result)
    }

    @Test
    fun `validateGuess accepts valid guess`() {
        val settings = GameSettings(digits = 4, allowRepeats = false, allowLeadingZero = false)
        val result = GameEngine.validateGuess("1234", settings)

        assertEquals(ValidationResult.Valid, result)
        assertTrue(result.isValid)
    }

    @Test
    fun `isWon returns true when all bulls`() {
        assertTrue(GameEngine.isWon(4, 4))
    }

    @Test
    fun `isWon returns false when not all bulls`() {
        assertFalse(GameEngine.isWon(3, 4))
        assertFalse(GameEngine.isWon(0, 4))
    }

    @Test
    fun `generateAllValidSecrets creates correct count for 3 digits no repeats no leading zero`() {
        val settings = GameSettings(digits = 3, allowRepeats = false, allowLeadingZero = false)
        val secrets = GameEngine.generateAllValidSecrets(settings)

        // 9 choices for first digit (1-9)
        // 9 choices for second digit (0-9 except first)
        // 8 choices for third digit (0-9 except first two)
        // Total: 9 * 9 * 8 = 648
        assertEquals(648, secrets.size)

        // Verify all are unique and valid
        assertEquals(secrets.size, secrets.toSet().size)
        secrets.forEach { secret ->
            assertEquals(ValidationResult.Valid, GameEngine.validateGuess(secret, settings))
        }
    }

    @Test
    fun `generateAllValidSecrets creates correct count for 4 digits no repeats no leading zero`() {
        val settings = GameSettings(digits = 4, allowRepeats = false, allowLeadingZero = false)
        val secrets = GameEngine.generateAllValidSecrets(settings)

        // 9 * 9 * 8 * 7 = 4536
        assertEquals(4536, secrets.size)
    }

    @Test
    fun `ValidationResult provides correct error messages`() {
        assertEquals(null, ValidationResult.Valid.getErrorMessage())
        assertNotNull(ValidationResult.InvalidLength.getErrorMessage())
        assertNotNull(ValidationResult.RepeatedDigits.getErrorMessage())
        assertNotNull(ValidationResult.LeadingZero.getErrorMessage())
        assertNotNull(ValidationResult.NonNumeric.getErrorMessage())
    }
}
