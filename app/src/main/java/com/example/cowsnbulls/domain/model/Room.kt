package com.example.cowsnbulls.domain.model

/**
 * Represents a multiplayer game room
 */
data class Room(
    val roomCode: String = "",
    val player1: PlayerData? = null,
    val player2: PlayerData? = null,
    val status: RoomStatus = RoomStatus.WAITING,
    val currentTurn: String = "", // player1 or player2
    val settings: GameSettings = GameSettings(),
    val createdAt: Long = System.currentTimeMillis()
) {
    fun isFull(): Boolean = player1 != null && player2 != null

    fun getOpponent(playerId: String): PlayerData? {
        return when (playerId) {
            "player1" -> player2
            "player2" -> player1
            else -> null
        }
    }

    fun getPlayer(playerId: String): PlayerData? {
        return when (playerId) {
            "player1" -> player1
            "player2" -> player2
            else -> null
        }
    }
}

/**
 * Player data within a room
 */
data class PlayerData(
    val name: String = "",
    val secret: String = "",
    val guesses: List<Guess> = emptyList(),
    val isReady: Boolean = false,
    val hasWon: Boolean = false
)

/**
 * Room status enum
 */
enum class RoomStatus {
    WAITING,    // Waiting for second player
    SETTING_UP, // Both players setting their secrets
    PLAYING,    // Game in progress
    FINISHED    // Game over
}
