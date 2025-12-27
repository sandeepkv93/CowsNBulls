package com.example.cowsnbulls.ui.navigation

import androidx.compose.runtime.Composable
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
import com.example.cowsnbulls.ui.screens.DifficultySelectScreen
import com.example.cowsnbulls.ui.screens.GameBoardScreen
import com.example.cowsnbulls.ui.screens.HomeScreen
import com.example.cowsnbulls.ui.screens.SettingsScreen
import com.example.cowsnbulls.ui.screens.VictoryScreen
import com.example.cowsnbulls.viewmodel.GameViewModel

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
                    // TODO: Navigate to multiplayer setup (Phase 3)
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
    }
}
