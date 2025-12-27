package com.example.cowsnbulls.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cowsnbulls.domain.model.Difficulty
import com.example.cowsnbulls.domain.model.GameMode
import com.example.cowsnbulls.domain.model.GameSettings
import com.example.cowsnbulls.ui.components.AnimatedGuessRow
import com.example.cowsnbulls.ui.components.CustomKeypad
import com.example.cowsnbulls.ui.components.DigitSlot
import com.example.cowsnbulls.ui.components.GuessRow
import com.example.cowsnbulls.ui.theme.BullsAndCowsTheme
import com.example.cowsnbulls.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameBoardScreen(
    viewModel: GameViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onGameWon: () -> Unit = {}
) {
    val gameState by viewModel.gameState.collectAsState()
    val validationError by viewModel.validationError.collectAsState()
    val listState = rememberLazyListState()

    // Auto-scroll to bottom-most attempt when new guess is added
    // Only scroll if the list is long enough to need scrolling
    LaunchedEffect(gameState.guessHistory.size) {
        if (gameState.guessHistory.isNotEmpty()) {
            // Small delay to let the AnimatedGuessRow animation start
            delay(50)
            // Check if the last item is not visible in the viewport
            val lastItemIndex = gameState.guessHistory.size - 1
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1

            // Only scroll if the last item is not fully visible
            if (lastItemIndex > lastVisibleIndex) {
                listState.animateScrollToItem(index = lastItemIndex)
            }
        }
    }

    // Navigate to victory screen when won
    LaunchedEffect(gameState.isWon) {
        if (gameState.isWon) {
            onGameWon()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Timer: ${viewModel.getFormattedTime()}")
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Opponent Panel
            OpponentPanel(
                opponent = gameState.opponent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Guess History
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                if (gameState.guessHistory.isEmpty()) {
                    item {
                        Text(
                            text = "Make your first guess!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    items(gameState.guessHistory) { guess ->
                        AnimatedGuessRow(guess = guess)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Current Guess Input
            CurrentGuessInput(
                guessNumber = gameState.nextGuessNumber,
                currentGuess = gameState.currentGuess,
                digitCount = gameState.settings.digits,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            // Validation Error
            if (validationError != null) {
                Text(
                    text = validationError!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            // Keypad
            CustomKeypad(
                usedDigits = gameState.usedDigits,
                onDigitClick = { digit -> viewModel.addDigit(digit) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.clearGuess() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear")
                }
                OutlinedButton(
                    onClick = { viewModel.removeLastDigit() },
                    modifier = Modifier.weight(1f),
                    enabled = gameState.currentGuess.isNotEmpty()
                ) {
                    Text("⌫ Delete")
                }
                Button(
                    onClick = { viewModel.submitGuess() },
                    modifier = Modifier.weight(1f),
                    enabled = gameState.isGuessComplete
                ) {
                    Text("Submit")
                }
            }
        }
    }
}

@Composable
fun OpponentPanel(
    opponent: com.example.cowsnbulls.domain.model.Opponent,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when (opponent) {
                is com.example.cowsnbulls.domain.model.Opponent.AI -> "🤖 AI Master"
                is com.example.cowsnbulls.domain.model.Opponent.Player -> "${opponent.avatar} ${opponent.name}"
            },
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Crack the code!",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun CurrentGuessInput(
    guessNumber: Int,
    currentGuess: String,
    digitCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your Guess #$guessNumber",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(digitCount) { index ->
                val digit = if (index < currentGuess.length) {
                    currentGuess[index].toString()
                } else {
                    null
                }
                DigitSlot(
                    digit = digit,
                    filled = digit != null
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameBoardScreenPreview() {
    BullsAndCowsTheme {
        val viewModel = GameViewModel()
        viewModel.startNewGame(
            mode = GameMode.VS_COMPUTER,
            difficulty = Difficulty.MEDIUM,
            settings = GameSettings()
        )
        GameBoardScreen(viewModel = viewModel)
    }
}
