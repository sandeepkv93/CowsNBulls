package com.example.cowsnbulls.ui.navigation

/**
 * Navigation routes for all screens
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object DifficultySelect : Screen("difficulty_select")
    object GameBoard : Screen("game_board/{difficulty}/{digits}/{allowRepeats}/{allowLeadingZero}") {
        fun createRoute(
            difficulty: String,
            digits: Int = 4,
            allowRepeats: Boolean = false,
            allowLeadingZero: Boolean = false
        ): String {
            return "game_board/$difficulty/$digits/$allowRepeats/$allowLeadingZero"
        }
    }
    object Victory : Screen("victory/{attempts}/{time}")  {
        fun createRoute(attempts: Int, time: Long): String {
            return "victory/$attempts/$time"
        }
    }
    object Settings : Screen("settings")
    object MultiplayerSetup : Screen("multiplayer_setup")
    object SecretSetup : Screen("secret_setup/{roomCode}/{playerId}/{digits}/{allowRepeats}/{allowLeadingZero}") {
        fun createRoute(
            roomCode: String,
            playerId: String,
            digits: Int,
            allowRepeats: Boolean,
            allowLeadingZero: Boolean
        ): String {
            return "secret_setup/$roomCode/$playerId/$digits/$allowRepeats/$allowLeadingZero"
        }
    }
    object MultiplayerGame : Screen("multiplayer_game/{roomCode}/{playerId}") {
        fun createRoute(roomCode: String, playerId: String): String {
            return "multiplayer_game/$roomCode/$playerId"
        }
    }
}
