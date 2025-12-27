package com.example.cowsnbulls.domain.engine

import com.example.cowsnbulls.domain.model.Difficulty
import com.example.cowsnbulls.domain.model.GameSettings
import com.example.cowsnbulls.domain.model.Guess
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AIOpponentTest {

    private lateinit var settings: GameSettings

    @Before
    fun setup() {
        settings = GameSettings(digits = 4, allowRepeats = false, allowLeadingZero = false)
    }

    @Test
    fun `AIOpponent Easy generates valid guesses`() {
        val ai = AIOpponent(Difficulty.EASY)
        ai.initializeCandidates(settings)

        val guess = ai.makeGuess(settings, emptyList())

        // Verify guess is valid
        assertEquals(4, guess.length)
        assertTrue(guess.all { it.isDigit() })
    }

    @Test
    fun `AIOpponent Medium generates valid guesses`() {
        val ai = AIOpponent(Difficulty.MEDIUM)
        ai.initializeCandidates(settings)

        val guess = ai.makeGuess(settings, emptyList())

        // Verify guess is valid
        assertEquals(4, guess.length)
        assertTrue(guess.all { it.isDigit() })
    }

    @Test
    fun `AIOpponent Hard generates valid guesses`() {
        val ai = AIOpponent(Difficulty.HARD)
        ai.initializeCandidates(settings)

        val guess = ai.makeGuess(settings, emptyList())

        // Verify guess is valid
        assertEquals(4, guess.length)
        assertTrue(guess.all { it.isDigit() })
    }

    @Test
    fun `AIOpponent Hard uses strategic first guess for 4 digits`() {
        val ai = AIOpponent(Difficulty.HARD)
        ai.initializeCandidates(settings)

        val guess = ai.makeGuess(settings, emptyList())

        // Hard difficulty should use "1234" as first guess for 4 digits
        assertEquals("1234", guess)
    }

    @Test
    fun `AIOpponent Medium filters candidates based on feedback`() {
        val ai = AIOpponent(Difficulty.MEDIUM)
        ai.initializeCandidates(settings)

        val initialCandidates = ai.getRemainingCandidates()
        assertTrue(initialCandidates > 0)

        // Provide feedback that should eliminate many candidates
        val history = listOf(
            Guess(number = 1, digits = "1234", bulls = 1, cows = 2)
        )

        val guess = ai.makeGuess(settings, history)

        // After filtering, candidates should be reduced
        val remainingCandidates = ai.getRemainingCandidates()
        assertTrue(remainingCandidates < initialCandidates)

        // Guess should be valid
        assertEquals(4, guess.length)
        assertTrue(guess.all { it.isDigit() })
    }

    @Test
    fun `AIOpponent Smart filters eliminate impossible candidates`() {
        val ai = AIOpponent(Difficulty.MEDIUM)
        ai.initializeCandidates(settings)

        // If secret is "5678" and we guess "1234" with 0 bulls, 0 cows
        // Then any candidate containing 1,2,3,4 should be eliminated
        val history = listOf(
            Guess(number = 1, digits = "1234", bulls = 0, cows = 0)
        )

        val guess = ai.makeGuess(settings, history)

        // Verify guess doesn't contain any of 1,2,3,4
        assertFalse(guess.contains('1'))
        assertFalse(guess.contains('2'))
        assertFalse(guess.contains('3'))
        assertFalse(guess.contains('4'))
    }

    @Test
    fun `AIOpponent narrows down with multiple guesses`() {
        val ai = AIOpponent(Difficulty.HARD)
        ai.initializeCandidates(settings)

        // Simulate multiple rounds of feedback
        val history = listOf(
            Guess(number = 1, digits = "1234", bulls = 1, cows = 1),
            Guess(number = 2, digits = "5678", bulls = 2, cows = 0)
        )

        val guess = ai.makeGuess(settings, history)

        // Should have significantly fewer candidates
        val remainingCandidates = ai.getRemainingCandidates()
        assertTrue(remainingCandidates < 100) // Much smaller search space

        // Guess should still be valid
        assertEquals(4, guess.length)
    }

    @Test
    fun `AIOpponent reset clears candidates`() {
        val ai = AIOpponent(Difficulty.MEDIUM)
        ai.initializeCandidates(settings)

        assertTrue(ai.getRemainingCandidates() > 0)

        ai.reset()

        assertEquals(0, ai.getRemainingCandidates())
    }

    @Test
    fun `AIOpponent handles different digit counts`() {
        val settings3 = GameSettings(digits = 3, allowRepeats = false, allowLeadingZero = false)
        val ai = AIOpponent(Difficulty.MEDIUM)
        ai.initializeCandidates(settings3)

        val guess = ai.makeGuess(settings3, emptyList())

        assertEquals(3, guess.length)
        assertTrue(guess.all { it.isDigit() })
    }

    @Test
    fun `AIOpponent with repeats allowed generates valid guesses`() {
        val settingsWithRepeats = GameSettings(digits = 4, allowRepeats = true, allowLeadingZero = false)
        val ai = AIOpponent(Difficulty.MEDIUM)
        ai.initializeCandidates(settingsWithRepeats)

        val guess = ai.makeGuess(settingsWithRepeats, emptyList())

        assertEquals(4, guess.length)
        assertTrue(guess.all { it.isDigit() })
    }

    @Test
    fun `AIOpponent converges to solution with perfect feedback`() {
        val ai = AIOpponent(Difficulty.HARD)
        ai.initializeCandidates(settings)

        // Secret is "5678"
        // Give increasingly accurate feedback
        val history = listOf(
            Guess(number = 1, digits = "1234", bulls = 0, cows = 0),
            Guess(number = 2, digits = "5678", bulls = 4, cows = 0) // Perfect match
        )

        val guess = ai.makeGuess(settings, history)

        // With 4 bulls feedback, only one candidate should remain
        assertEquals(1, ai.getRemainingCandidates())
        assertEquals("5678", guess)
    }
}
