package com.example.cowsnbulls.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cowsnbulls.data.remote.FirebaseGameRepository
import com.example.cowsnbulls.domain.engine.GameEngine
import com.example.cowsnbulls.domain.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for multiplayer game management
 */
class MultiplayerViewModel : ViewModel() {

    private val repository = FirebaseGameRepository()

    // Setup state
    private val _setupState = MutableStateFlow<MultiplayerSetupState>(MultiplayerSetupState.Idle)
    val setupState: StateFlow<MultiplayerSetupState> = _setupState.asStateFlow()

    // Room state
    private val _roomState = MutableStateFlow<Room?>(null)
    val roomState: StateFlow<Room?> = _roomState.asStateFlow()

    // Current player info
    private val _playerId = MutableStateFlow<String?>(null)
    val playerId: StateFlow<String?> = _playerId.asStateFlow()

    private val _roomCode = MutableStateFlow<String?>(null)
    val roomCode: StateFlow<String?> = _roomCode.asStateFlow()

    // Game state for multiplayer
    private val _currentGuess = MutableStateFlow("")
    val currentGuess: StateFlow<String> = _currentGuess.asStateFlow()

    private val _usedDigits = MutableStateFlow<Set<String>>(emptySet())
    val usedDigits: StateFlow<Set<String>> = _usedDigits.asStateFlow()

    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError.asStateFlow()

    /**
     * Create a new room
     */
    fun createRoom(playerName: String, settings: GameSettings) {
        viewModelScope.launch {
            _setupState.value = MultiplayerSetupState.Loading
            val result = repository.createRoom(playerName, settings)

            result.fold(
                onSuccess = { (code, playerId) ->
                    _roomCode.value = code
                    _playerId.value = playerId
                    _setupState.value = MultiplayerSetupState.RoomCreated(code, playerId)
                    observeRoom(code)
                },
                onFailure = { error ->
                    _setupState.value = MultiplayerSetupState.Error(error.message ?: "Failed to create room")
                }
            )
        }
    }

    /**
     * Join an existing room
     */
    fun joinRoom(roomCode: String, playerName: String) {
        viewModelScope.launch {
            _setupState.value = MultiplayerSetupState.Loading
            val result = repository.joinRoom(roomCode, playerName)

            result.fold(
                onSuccess = { playerId ->
                    _roomCode.value = roomCode
                    _playerId.value = playerId
                    _setupState.value = MultiplayerSetupState.RoomJoined(roomCode, playerId)
                    observeRoom(roomCode)
                },
                onFailure = { error ->
                    _setupState.value = MultiplayerSetupState.Error(error.message ?: "Failed to join room")
                }
            )
        }
    }

    /**
     * Set player's secret
     */
    fun setSecret(secret: String) {
        viewModelScope.launch {
            val code = _roomCode.value ?: return@launch
            val pId = _playerId.value ?: return@launch

            repository.setSecret(code, pId, secret)
        }
    }

    /**
     * Submit a guess in multiplayer
     */
    fun submitGuess() {
        viewModelScope.launch {
            val code = _roomCode.value ?: return@launch
            val pId = _playerId.value ?: return@launch
            val room = _roomState.value ?: return@launch
            val opponent = room.getOpponent(pId) ?: return@launch

            // Validate guess
            val validationResult = GameEngine.validateGuess(_currentGuess.value, room.settings)
            if (!validationResult.isValid) {
                _validationError.value = validationResult.getErrorMessage()
                return@launch
            }

            // Calculate feedback
            val (bulls, cows) = GameEngine.calculateFeedback(opponent.secret, _currentGuess.value)

            val player = room.getPlayer(pId)
            val guessNumber = (player?.guesses?.size ?: 0) + 1

            val guess = Guess(
                number = guessNumber,
                digits = _currentGuess.value,
                bulls = bulls,
                cows = cows
            )

            val hasWon = GameEngine.isWon(bulls, room.settings.digits)

            // Submit to Firebase
            repository.submitGuess(code, pId, guess, hasWon)

            // Clear current guess
            clearGuess()
        }
    }

    /**
     * Add digit to current guess
     */
    fun addDigit(digit: String) {
        val room = _roomState.value ?: return
        if (_currentGuess.value.length < room.settings.digits) {
            _currentGuess.value += digit
            if (!room.settings.allowRepeats) {
                _usedDigits.value = _usedDigits.value + digit
            }
            _validationError.value = null
        }
    }

    /**
     * Remove last digit
     */
    fun removeLastDigit() {
        if (_currentGuess.value.isNotEmpty()) {
            val removedDigit = _currentGuess.value.last().toString()
            _currentGuess.value = _currentGuess.value.dropLast(1)

            val room = _roomState.value
            if (room?.settings?.allowRepeats == false) {
                _usedDigits.value = _usedDigits.value - removedDigit
            }
        }
    }

    /**
     * Clear current guess
     */
    fun clearGuess() {
        _currentGuess.value = ""
        _usedDigits.value = emptySet()
        _validationError.value = null
    }

    /**
     * Observe room changes
     */
    private fun observeRoom(roomCode: String) {
        viewModelScope.launch {
            repository.observeRoom(roomCode).collect { room ->
                _roomState.value = room
            }
        }
    }

    /**
     * Leave room
     */
    fun leaveRoom() {
        viewModelScope.launch {
            val code = _roomCode.value ?: return@launch
            val pId = _playerId.value ?: return@launch
            repository.leaveRoom(code, pId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        leaveRoom()
    }
}

/**
 * States for multiplayer setup
 */
sealed class MultiplayerSetupState {
    object Idle : MultiplayerSetupState()
    object Loading : MultiplayerSetupState()
    data class RoomCreated(val roomCode: String, val playerId: String) : MultiplayerSetupState()
    data class RoomJoined(val roomCode: String, val playerId: String) : MultiplayerSetupState()
    data class Error(val message: String) : MultiplayerSetupState()
}
