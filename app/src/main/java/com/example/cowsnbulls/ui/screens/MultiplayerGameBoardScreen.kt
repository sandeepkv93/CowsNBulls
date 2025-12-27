package com.example.cowsnbulls.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cowsnbulls.domain.model.RoomStatus
import com.example.cowsnbulls.ui.components.AnimatedGuessRow
import com.example.cowsnbulls.ui.components.CustomKeypad
import com.example.cowsnbulls.ui.components.DigitSlot
import com.example.cowsnbulls.viewmodel.MultiplayerViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiplayerGameBoardScreen(
    roomCode: String,
    playerId: String,
    viewModel: MultiplayerViewModel = viewModel(),
    onBackClick: () -> Unit,
    onGameFinished: (winner: String?, attempts: Int) -> Unit
) {
    val roomState by viewModel.roomState.collectAsState()
    val currentGuess by viewModel.currentGuess.collectAsState()
    val usedDigits by viewModel.usedDigits.collectAsState()
    val validationError by viewModel.validationError.collectAsState()

    val listState = rememberLazyListState()

    val room = roomState
    val player = room?.getPlayer(playerId)
    val opponent = room?.getOpponent(playerId)
    val isMyTurn = room?.currentTurn == playerId

    // Check for game finish
    LaunchedEffect(room?.status) {
        if (room?.status == RoomStatus.FINISHED) {
            val winner = when {
                player?.hasWon == true -> player.name
                opponent?.hasWon == true -> opponent.name
                else -> null
            }
            onGameFinished(winner, player?.guesses?.size ?: 0)
        }
    }

    // Auto-scroll when new guess added
    LaunchedEffect(player?.guesses?.size) {
        if (player?.guesses?.isNotEmpty() == true) {
            delay(50)
            val lastItemIndex = (player.guesses.size - 1).coerceAtLeast(0)
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1

            if (lastItemIndex > lastVisibleIndex) {
                listState.animateScrollToItem(index = lastItemIndex)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Room: $roomCode", fontSize = 16.sp)
                        Text(
                            text = if (isMyTurn) "Your Turn" else "${opponent?.name}'s Turn",
                            fontSize = 12.sp,
                            color = if (isMyTurn) Color(0xFF4CAF50) else Color(0xFFFF9800)
                        )
                    }
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
        if (room == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Opponent Status
                OpponentStatus(
                    opponentName = opponent?.name ?: "Opponent",
                    opponentGuesses = opponent?.guesses?.size ?: 0,
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
                    if (player?.guesses?.isEmpty() == true) {
                        item {
                            Text(
                                text = if (isMyTurn) "Make your first guess!" else "Wait for your turn...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    } else {
                        items(player?.guesses ?: emptyList()) { guess ->
                            AnimatedGuessRow(guess = guess)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Current Guess Input
                CurrentGuessInput(
                    guessNumber = (player?.guesses?.size ?: 0) + 1,
                    currentGuess = currentGuess,
                    digitCount = room.settings.digits,
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

                // Keypad and buttons
                if (isMyTurn) {
                    CustomKeypad(
                        usedDigits = usedDigits,
                        onDigitClick = { digit -> viewModel.addDigit(digit) },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

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
                            enabled = currentGuess.isNotEmpty()
                        ) {
                            Text("⌫ Delete")
                        }
                        Button(
                            onClick = { viewModel.submitGuess() },
                            modifier = Modifier.weight(1f),
                            enabled = currentGuess.length == room.settings.digits
                        ) {
                            Text("Submit")
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Waiting for ${opponent?.name ?: "opponent"}...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OpponentStatus(
    opponentName: String,
    opponentGuesses: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "👤 $opponentName",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "Trying to crack your code",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = "$opponentGuesses\nattempts",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
