package com.example.cowsnbulls.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cowsnbulls.domain.model.Difficulty
import com.example.cowsnbulls.domain.model.GameMode
import com.example.cowsnbulls.domain.model.GameSettings
import com.example.cowsnbulls.ui.screens.*
import com.example.cowsnbulls.viewmodel.GameViewModel
import com.example.cowsnbulls.viewmodel.MultiplayerViewModel
import com.example.cowsnbulls.viewmodel.MultiplayerSetupState

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                onVsComputerClick = {
                    navController.navigate(Screen.DifficultySelect.route)
                },
                onPlayWithFriendClick = {
                    navController.navigate(Screen.MultiplayerSetup.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        // Difficulty Select Screen
        composable(Screen.DifficultySelect.route) {
            DifficultySelectScreen(
                onDifficultySelected = { difficulty, settings ->
                    navController.navigate(
                        Screen.GameBoard.createRoute(
                            difficulty = difficulty.name,
                            digits = settings.digits,
                            allowRepeats = settings.allowRepeats,
                            allowLeadingZero = settings.allowLeadingZero
                        )
                    )
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Game Board Screen
        composable(
            route = Screen.GameBoard.route,
            arguments = listOf(
                navArgument("difficulty") { type = NavType.StringType },
                navArgument("digits") { type = NavType.IntType },
                navArgument("allowRepeats") { type = NavType.BoolType },
                navArgument("allowLeadingZero") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val difficulty = Difficulty.valueOf(
                backStackEntry.arguments?.getString("difficulty") ?: "MEDIUM"
            )
            val digits = backStackEntry.arguments?.getInt("digits") ?: 4
            val allowRepeats = backStackEntry.arguments?.getBoolean("allowRepeats") ?: false
            val allowLeadingZero = backStackEntry.arguments?.getBoolean("allowLeadingZero") ?: false

            val settings = GameSettings(
                digits = digits,
                allowRepeats = allowRepeats,
                allowLeadingZero = allowLeadingZero
            )

            val viewModel: GameViewModel = viewModel()

            // Start game when entering screen
            androidx.compose.runtime.LaunchedEffect(Unit) {
                viewModel.startNewGame(
                    mode = GameMode.VS_COMPUTER,
                    difficulty = difficulty,
                    settings = settings
                )
            }

            GameBoardScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onGameWon = {
                    val state = viewModel.gameState.value
                    navController.navigate(
                        Screen.Victory.createRoute(
                            attempts = state.guessHistory.size,
                            time = state.elapsedTime
                        )
                    ) {
                        // Remove game board from back stack
                        popUpTo(Screen.DifficultySelect.route)
                    }
                }
            )
        }

        // Victory Screen
        composable(
            route = Screen.Victory.route,
            arguments = listOf(
                navArgument("attempts") { type = NavType.IntType },
                navArgument("time") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val attempts = backStackEntry.arguments?.getInt("attempts") ?: 0
            val time = backStackEntry.arguments?.getLong("time") ?: 0L

            VictoryScreen(
                attempts = attempts,
                elapsedTime = time,
                onPlayAgainClick = {
                    navController.navigate(Screen.DifficultySelect.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onHomeClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = false
                        }
                    }
                }
            )
        }

        // Settings Screen
        composable(Screen.Settings.route) {
            SettingsScreen(
                currentSettings = GameSettings(), // TODO: Load from SettingsManager in real implementation
                onSaveSettings = { settings ->
                    // TODO: Save via SettingsManager in real implementation
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Multiplayer Setup Screen
        composable(Screen.MultiplayerSetup.route) {
            val viewModel: MultiplayerViewModel = viewModel()
            val setupState by viewModel.setupState.collectAsState()
            val roomCode by viewModel.roomCode.collectAsState()
            val playerId by viewModel.playerId.collectAsState()

            // Navigate to secret setup when room is created/joined
            LaunchedEffect(setupState) {
                when (val state = setupState) {
                    is MultiplayerSetupState.RoomCreated -> {
                        navController.navigate(
                            Screen.SecretSetup.createRoute(
                                roomCode = state.roomCode,
                                playerId = state.playerId,
                                digits = 4, // TODO: Get from settings
                                allowRepeats = false,
                                allowLeadingZero = false
                            )
                        )
                    }
                    is MultiplayerSetupState.RoomJoined -> {
                        navController.navigate(
                            Screen.SecretSetup.createRoute(
                                roomCode = state.roomCode,
                                playerId = state.playerId,
                                digits = 4,
                                allowRepeats = false,
                                allowLeadingZero = false
                            )
                        )
                    }
                    else -> {}
                }
            }

            MultiplayerSetupScreen(
                onCreateRoom = { playerName ->
                    viewModel.createRoom(playerName, GameSettings())
                },
                onJoinRoom = { code, playerName ->
                    viewModel.joinRoom(code, playerName)
                },
                onBackClick = {
                    navController.popBackStack()
                },
                isLoading = setupState is MultiplayerSetupState.Loading,
                errorMessage = (setupState as? MultiplayerSetupState.Error)?.message
            )
        }

        // Secret Setup Screen
        composable(
            route = Screen.SecretSetup.route,
            arguments = listOf(
                navArgument("roomCode") { type = NavType.StringType },
                navArgument("playerId") { type = NavType.StringType },
                navArgument("digits") { type = NavType.IntType },
                navArgument("allowRepeats") { type = NavType.BoolType },
                navArgument("allowLeadingZero") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val roomCode = backStackEntry.arguments?.getString("roomCode") ?: ""
            val playerId = backStackEntry.arguments?.getString("playerId") ?: ""
            val digits = backStackEntry.arguments?.getInt("digits") ?: 4
            val allowRepeats = backStackEntry.arguments?.getBoolean("allowRepeats") ?: false
            val allowLeadingZero = backStackEntry.arguments?.getBoolean("allowLeadingZero") ?: false

            val settings = GameSettings(digits, allowRepeats, allowLeadingZero)

            val viewModel: MultiplayerViewModel = viewModel()
            val roomState by viewModel.roomState.collectAsState()

            val room = roomState
            val player = room?.getPlayer(playerId)
            val opponent = room?.getOpponent(playerId)

            // Navigate to game when both players are ready
            LaunchedEffect(room?.status) {
                if (room?.status == com.example.cowsnbulls.domain.model.RoomStatus.PLAYING) {
                    navController.navigate(
                        Screen.MultiplayerGame.createRoute(roomCode, playerId)
                    ) {
                        popUpTo(Screen.MultiplayerSetup.route)
                    }
                }
            }

            SecretSetupScreen(
                roomCode = roomCode,
                playerName = player?.name ?: "You",
                opponentName = opponent?.name,
                settings = settings,
                onSecretSet = { secret ->
                    viewModel.setSecret(secret)
                },
                isWaitingForOpponent = player?.isReady == true && opponent?.isReady == false
            )
        }

        // Multiplayer Game Board Screen
        composable(
            route = Screen.MultiplayerGame.route,
            arguments = listOf(
                navArgument("roomCode") { type = NavType.StringType },
                navArgument("playerId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val roomCode = backStackEntry.arguments?.getString("roomCode") ?: ""
            val playerId = backStackEntry.arguments?.getString("playerId") ?: ""

            val viewModel: MultiplayerViewModel = viewModel()

            MultiplayerGameBoardScreen(
                roomCode = roomCode,
                playerId = playerId,
                viewModel = viewModel,
                onBackClick = {
                    viewModel.leaveRoom()
                    navController.popBackStack(Screen.Home.route, false)
                },
                onGameFinished = { winner, attempts ->
                    // Navigate to victory screen
                    navController.navigate(
                        Screen.Victory.createRoute(
                            attempts = attempts,
                            time = 0 // Multiplayer doesn't track time currently
                        )
                    ) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }
    }
}
