package com.example.cowsnbulls.data.remote

import com.example.cowsnbulls.domain.model.*
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

/**
 * Repository for Firebase Realtime Database operations
 */
class FirebaseGameRepository {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val roomsRef: DatabaseReference = database.child("rooms")

    /**
     * Generate a unique 6-character room code
     */
    private fun generateRoomCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6)
            .map { chars[Random.nextInt(chars.length)] }
            .joinToString("")
    }

    /**
     * Create a new room
     */
    suspend fun createRoom(playerName: String, settings: GameSettings): Result<Pair<String, String>> {
        return try {
            val roomCode = generateRoomCode()
            val room = Room(
                roomCode = roomCode,
                player1 = PlayerData(name = playerName),
                status = RoomStatus.WAITING,
                settings = settings
            )

            roomsRef.child(roomCode).setValue(room).await()
            Result.success(Pair(roomCode, "player1"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Join an existing room
     */
    suspend fun joinRoom(roomCode: String, playerName: String): Result<String> {
        return try {
            val snapshot = roomsRef.child(roomCode).get().await()

            if (!snapshot.exists()) {
                return Result.failure(Exception("Room not found"))
            }

            val room = snapshot.getValue(Room::class.java)
            if (room == null) {
                return Result.failure(Exception("Invalid room data"))
            }

            if (room.isFull()) {
                return Result.failure(Exception("Room is full"))
            }

            // Add player2
            roomsRef.child(roomCode).child("player2").setValue(
                PlayerData(name = playerName)
            ).await()

            // Update room status
            roomsRef.child(roomCode).child("status").setValue(RoomStatus.SETTING_UP.name).await()

            Result.success("player2")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Set player's secret number
     */
    suspend fun setSecret(roomCode: String, playerId: String, secret: String): Result<Unit> {
        return try {
            roomsRef.child(roomCode).child(playerId).child("secret").setValue(secret).await()
            roomsRef.child(roomCode).child(playerId).child("isReady").setValue(true).await()

            // Check if both players are ready
            val snapshot = roomsRef.child(roomCode).get().await()
            val room = snapshot.getValue(Room::class.java)

            if (room?.player1?.isReady == true && room.player2?.isReady == true) {
                // Start the game
                roomsRef.child(roomCode).child("status").setValue(RoomStatus.PLAYING.name).await()
                roomsRef.child(roomCode).child("currentTurn").setValue("player1").await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Submit a guess
     */
    suspend fun submitGuess(
        roomCode: String,
        playerId: String,
        guess: Guess,
        hasWon: Boolean
    ): Result<Unit> {
        return try {
            // Add guess to player's guess list
            val guessesRef = roomsRef.child(roomCode).child(playerId).child("guesses")
            guessesRef.push().setValue(guess).await()

            if (hasWon) {
                // Mark player as winner
                roomsRef.child(roomCode).child(playerId).child("hasWon").setValue(true).await()
                roomsRef.child(roomCode).child("status").setValue(RoomStatus.FINISHED.name).await()
            } else {
                // Switch turns
                val nextTurn = if (playerId == "player1") "player2" else "player1"
                roomsRef.child(roomCode).child("currentTurn").setValue(nextTurn).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Observe room changes in real-time
     */
    fun observeRoom(roomCode: String): Flow<Room?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val room = snapshot.getValue(Room::class.java)
                trySend(room)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        roomsRef.child(roomCode).addValueEventListener(listener)

        awaitClose {
            roomsRef.child(roomCode).removeEventListener(listener)
        }
    }

    /**
     * Leave room (cleanup)
     */
    suspend fun leaveRoom(roomCode: String, playerId: String): Result<Unit> {
        return try {
            val snapshot = roomsRef.child(roomCode).get().await()
            val room = snapshot.getValue(Room::class.java)

            if (room != null) {
                if (room.status == RoomStatus.WAITING || room.status == RoomStatus.SETTING_UP) {
                    // If game hasn't started, delete the room
                    roomsRef.child(roomCode).removeValue().await()
                } else {
                    // Mark room as finished
                    roomsRef.child(roomCode).child("status").setValue(RoomStatus.FINISHED.name).await()
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
