package com.example.cowsnbulls.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cowsnbulls.domain.model.Guess
import com.example.cowsnbulls.ui.theme.BullsAndCowsTheme
import com.example.cowsnbulls.ui.theme.BullsGreen40
import com.example.cowsnbulls.ui.theme.CowsAmber40

/**
 * Single guess row showing guess number, digits, and feedback
 */
@Composable
fun GuessRow(
    guess: Guess,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Guess number
        Text(
            text = "#${guess.number}",
            color = Color.Gray,
            modifier = Modifier.padding(end = 8.dp)
        )

        // Digit slots
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            guess.digits.forEach { digit ->
                DigitSlot(
                    digit = digit.toString(),
                    filled = true
                )
            }
        }

        // Feedback pills
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FeedbackPill(
                icon = "🐂",
                count = guess.bulls,
                color = BullsGreen40
            )
            FeedbackPill(
                icon = "🐮",
                count = guess.cows,
                color = CowsAmber40
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GuessRowPreview() {
    BullsAndCowsTheme {
        GuessRow(
            guess = Guess(
                number = 1,
                digits = "1234",
                bulls = 2,
                cows = 1
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GuessRowWinPreview() {
    BullsAndCowsTheme {
        GuessRow(
            guess = Guess(
                number = 5,
                digits = "5678",
                bulls = 4,
                cows = 0
            )
        )
    }
}
