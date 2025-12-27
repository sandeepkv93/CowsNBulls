package com.example.cowsnbulls.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cowsnbulls.domain.engine.GameEngine
import com.example.cowsnbulls.domain.engine.ValidationResult
import com.example.cowsnbulls.domain.model.Difficulty
import com.example.cowsnbulls.domain.model.GameMode
import com.example.cowsnbulls.domain.model.GameSettings
import com.example.cowsnbulls.domain.model.GameState
import com.example.cowsnbulls.domain.model.Guess
import com.example.cowsnbulls.domain.model.Opponent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError.asStateFlow()

    private var timerJob: Job? = null

    /**
     * Start a new game with given settings and difficulty
     */
    fun startNewGame(
        mode: GameMode = GameMode.VS_COMPUTER,
        difficulty: Difficulty = Difficulty.MEDIUM,
        settings: GameSettings = GameSettings()
    ) {
        // Generate secret
        val secret = GameEngine.generateSecret(settings)

        _gameState.value = GameState(
            mode = mode,
            difficulty = difficulty,
            settings = settings,
            secret = secret,
            currentGuess = "",
            guessHistory = emptyList(),
            usedDigits = emptySet(),
            elapsedTime = 0,
            isWon = false,
            opponent = Opponent.AI
        )

        // Start timer
        startTimer()
    }

    /**
     * Add a digit to current guess
     */
    fun addDigit(digit: String) {
        _validationError.value = null

        _gameState.update { state ->
            if (state.currentGuess.length < state.settings.digits) {
                val newGuess = state.currentGuess + digit
                val newUsedDigits = if (!state.settings.allowRepeats) {
                    state.usedDigits + digit
                } else {
                    state.usedDigits
                }

                state.copy(
                    currentGuess = newGuess,
                    usedDigits = newUsedDigits
                )
            } else {
                state
            }
        }
    }

    /**
     * Remove last digit from current guess
     */
    fun removeLastDigit() {
        _validationError.value = null

        _gameState.update { state ->
            if (state.currentGuess.isNotEmpty()) {
                val removedDigit = state.currentGuess.last().toString()
                val newGuess = state.currentGuess.dropLast(1)
                val newUsedDigits = if (!state.settings.allowRepeats) {
                    state.usedDigits - removedDigit
                } else {
                    state.usedDigits
                }

                state.copy(
                    currentGuess = newGuess,
                    usedDigits = newUsedDigits
                )
            } else {
                state
            }
        }
    }

    /**
     * Clear current guess
     */
    fun clearGuess() {
        _validationError.value = null

        _gameState.update { state ->
            state.copy(
                currentGuess = "",
                usedDigits = emptySet()
            )
        }
    }

    /**
     * Submit current guess
     */
    fun submitGuess() {
        val state = _gameState.value

        // Validate guess
        val validationResult = GameEngine.validateGuess(state.currentGuess, state.settings)
        if (!validationResult.isValid) {
            _validationError.value = validationResult.getErrorMessage()
            return
        }

        // Calculate feedback
        val (bulls, cows) = GameEngine.calculateFeedback(state.secret, state.currentGuess)

        // Create guess object
        val guess = Guess(
            number = state.nextGuessNumber,
            digits = state.currentGuess,
            bulls = bulls,
            cows = cows
        )

        // Check if won
        val isWon = GameEngine.isWon(bulls, state.settings.digits)

        // Update state
        _gameState.update { currentState ->
            currentState.copy(
                guessHistory = currentState.guessHistory + guess,
                currentGuess = "",
                usedDigits = emptySet(),
                isWon = isWon
            )
        }

        // Stop timer if won
        if (isWon) {
            stopTimer()
        }

        _validationError.value = null
    }

    /**
     * Start the game timer
     */
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _gameState.update { it.copy(elapsedTime = it.elapsedTime + 1) }
            }
        }
    }

    /**
     * Stop the game timer
     */
    private fun stopTimer() {
        timerJob?.cancel()
    }

    /**
     * Format elapsed time as MM:SS
     */
    fun getFormattedTime(): String {
        val totalSeconds = _gameState.value.elapsedTime
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
