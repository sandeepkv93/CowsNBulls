package com.example.cowsnbulls.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cowsnbulls.domain.model.GameSettings
import com.example.cowsnbulls.ui.components.CustomKeypad
import com.example.cowsnbulls.ui.components.DigitSlot
import com.example.cowsnbulls.ui.theme.Purple40
import com.example.cowsnbulls.ui.theme.Purple80

@Composable
fun SecretSetupScreen(
    roomCode: String,
    playerName: String,
    opponentName: String?,
    settings: GameSettings,
    onSecretSet: (String) -> Unit = {},
    isWaitingForOpponent: Boolean = false
) {
    var secret by remember { mutableStateOf("") }
    var usedDigits by remember { mutableStateOf(setOf<String>()) }

    val isSecretComplete = secret.length == settings.digits

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Purple80.copy(alpha = 0.1f),
                        Color.White
                    )
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🔐",
            fontSize = 80.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Set Your Secret",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Room: $roomCode",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )

        if (opponentName != null) {
            Text(
                text = "Playing against: $opponentName",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        if (isWaitingForOpponent) {
            // Waiting state
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Waiting for $opponentName to set their secret...",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
                Text(
                    text = "Your secret: $secret",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        } else {
            // Secret input UI
            Text(
                text = "Enter a ${settings.digits}-digit secret",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Secret Display
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                repeat(settings.digits) { index ->
                    val digit = if (index < secret.length) {
                        secret[index].toString()
                    } else {
                        null
                    }
                    DigitSlot(
                        digit = digit,
                        filled = digit != null
                    )
                }
            }

            // Keypad
            CustomKeypad(
                usedDigits = if (settings.allowRepeats) emptySet() else usedDigits,
                onDigitClick = { digit ->
                    if (secret.length < settings.digits) {
                        secret += digit
                        if (!settings.allowRepeats) {
                            usedDigits = usedDigits + digit
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (secret.isNotEmpty()) {
                            val removedDigit = secret.last().toString()
                            secret = secret.dropLast(1)
                            if (!settings.allowRepeats) {
                                usedDigits = usedDigits - removedDigit
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = secret.isNotEmpty()
                ) {
                    Text("⌫ Delete")
                }

                Button(
                    onClick = { onSecretSet(secret) },
                    modifier = Modifier.weight(1f),
                    enabled = isSecretComplete
                ) {
                    Text("Confirm", fontSize = 16.sp)
                }
            }
        }
    }
}
