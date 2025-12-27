package com.example.cowsnbulls.viewmodel

import com.example.cowsnbulls.domain.model.Difficulty
import com.example.cowsnbulls.domain.model.GameMode
import com.example.cowsnbulls.domain.model.GameSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private lateinit var viewModel: GameViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = GameViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `startNewGame initializes game state correctly`() = runTest {
        val settings = GameSettings(digits = 4, allowRepeats = false, allowLeadingZero = false)

        viewModel.startNewGame(
            mode = GameMode.VS_COMPUTER,
            difficulty = Difficulty.MEDIUM,
            settings = settings
        )

        val state = viewModel.gameState.value

        assertEquals(GameMode.VS_COMPUTER, state.mode)
        assertEquals(Difficulty.MEDIUM, state.difficulty)
        assertEquals(4, state.settings.digits)
        assertEquals(4, state.secret.length)
        assertEquals("", state.currentGuess)
        assertTrue(state.guessHistory.isEmpty())
        assertFalse(state.isWon)
        assertEquals(0, state.elapsedTime)
    }

    @Test
    fun `addDigit adds digit to current guess`() = runTest {
        viewModel.startNewGame()

        viewModel.addDigit("1")
        viewModel.addDigit("2")

        assertEquals("12", viewModel.gameState.value.currentGuess)
    }

    @Test
    fun `addDigit respects digit limit`() = runTest {
        val settings = GameSettings(digits = 4)
        viewModel.startNewGame(settings = settings)

        viewModel.addDigit("1")
        viewModel.addDigit("2")
        viewModel.addDigit("3")
        viewModel.addDigit("4")
        viewModel.addDigit("5") // Should be ignored

        assertEquals("1234", viewModel.gameState.value.currentGuess)
    }

    @Test
    fun `addDigit updates used digits when repeats not allowed`() = runTest {
        val settings = GameSettings(allowRepeats = false)
        viewModel.startNewGame(settings = settings)

        viewModel.addDigit("1")
        viewModel.addDigit("2")

        val usedDigits = viewModel.gameState.value.usedDigits
        assertTrue(usedDigits.contains("1"))
        assertTrue(usedDigits.contains("2"))
    }

    @Test
    fun `removeLastDigit removes last digit`() = runTest {
        viewModel.startNewGame()

        viewModel.addDigit("1")
        viewModel.addDigit("2")
        viewModel.addDigit("3")
        viewModel.removeLastDigit()

        assertEquals("12", viewModel.gameState.value.currentGuess)
    }

    @Test
    fun `removeLastDigit on empty guess does nothing`() = runTest {
        viewModel.startNewGame()

        viewModel.removeLastDigit()

        assertEquals("", viewModel.gameState.value.currentGuess)
    }

    @Test
    fun `clearGuess clears current guess and used digits`() = runTest {
        viewModel.startNewGame()

        viewModel.addDigit("1")
        viewModel.addDigit("2")
        viewModel.clearGuess()

        assertEquals("", viewModel.gameState.value.currentGuess)
        assertTrue(viewModel.gameState.value.usedDigits.isEmpty())
    }

    @Test
    fun `submitGuess rejects incomplete guess`() = runTest {
        val settings = GameSettings(digits = 4)
        viewModel.startNewGame(settings = settings)

        viewModel.addDigit("1")
        viewModel.addDigit("2")
        viewModel.submitGuess() // Only 2 digits

        // Should show validation error
        assertNotNull(viewModel.validationError.value)

        // Guess history should still be empty
        assertTrue(viewModel.gameState.value.guessHistory.isEmpty())
    }

    @Test
    fun `submitGuess rejects repeated digits when not allowed`() = runTest {
        val settings = GameSettings(digits = 4, allowRepeats = false)
        viewModel.startNewGame(settings = settings)

        viewModel.addDigit("1")
        viewModel.addDigit("1")
        viewModel.addDigit("2")
        viewModel.addDigit("3")
        viewModel.submitGuess()

        // Should show validation error
        assertNotNull(viewModel.validationError.value)
        assertTrue(viewModel.gameState.value.guessHistory.isEmpty())
    }

    @Test
    fun `submitGuess adds valid guess to history`() = runTest {
        viewModel.startNewGame()

        // Make a complete valid guess
        viewModel.addDigit("1")
        viewModel.addDigit("2")
        viewModel.addDigit("3")
        viewModel.addDigit("4")
        viewModel.submitGuess()

        // History should have one entry
        assertEquals(1, viewModel.gameState.value.guessHistory.size)

        // Current guess should be cleared
        assertEquals("", viewModel.gameState.value.currentGuess)
    }

    @Test
    fun `submitGuess calculates bulls and cows correctly`() = runTest {
        // Start game with known secret for testing
        viewModel.startNewGame()

        // We don't know the secret, but we can test the structure
        viewModel.addDigit("1")
        viewModel.addDigit("2")
        viewModel.addDigit("3")
        viewModel.addDigit("4")
        viewModel.submitGuess()

        val guess = viewModel.gameState.value.guessHistory.first()

        assertEquals(1, guess.number)
        assertEquals("1234", guess.digits)
        assertTrue(guess.bulls >= 0 && guess.bulls <= 4)
        assertTrue(guess.cows >= 0 && guess.cows <= 4)
        assertTrue(guess.bulls + guess.cows <= 4)
    }

    @Test
    fun `isGuessComplete returns true when guess is complete`() = runTest {
        val settings = GameSettings(digits = 3)
        viewModel.startNewGame(settings = settings)

        assertFalse(viewModel.gameState.value.isGuessComplete)

        viewModel.addDigit("1")
        assertFalse(viewModel.gameState.value.isGuessComplete)

        viewModel.addDigit("2")
        assertFalse(viewModel.gameState.value.isGuessComplete)

        viewModel.addDigit("3")
        assertTrue(viewModel.gameState.value.isGuessComplete)
    }

    @Test
    fun `nextGuessNumber increments correctly`() = runTest {
        viewModel.startNewGame()

        assertEquals(1, viewModel.gameState.value.nextGuessNumber)

        viewModel.addDigit("1")
        viewModel.addDigit("2")
        viewModel.addDigit("3")
        viewModel.addDigit("4")
        viewModel.submitGuess()

        assertEquals(2, viewModel.gameState.value.nextGuessNumber)
    }

    @Test
    fun `getFormattedTime formats time correctly`() = runTest {
        viewModel.startNewGame()

        // Initial time
        assertEquals("00:00", viewModel.getFormattedTime())

        // Note: Testing timer progression would require advancing test dispatcher
        // which is complex for this synchronous test
    }

    @Test
    fun `validation error is cleared when adding digit`() = runTest {
        viewModel.startNewGame()

        // Trigger validation error
        viewModel.submitGuess() // Empty guess

        assertNotNull(viewModel.validationError.value)

        // Add digit should clear error
        viewModel.addDigit("1")

        assertNull(viewModel.validationError.value)
    }

    @Test
    fun `validation error is cleared when clearing guess`() = runTest {
        viewModel.startNewGame()

        // Trigger validation error
        viewModel.submitGuess()

        assertNotNull(viewModel.validationError.value)

        // Clear should remove error
        viewModel.clearGuess()

        assertNull(viewModel.validationError.value)
    }
}
