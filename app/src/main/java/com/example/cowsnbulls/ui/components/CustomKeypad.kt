package com.example.cowsnbulls.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cowsnbulls.ui.theme.BullsAndCowsTheme

/**
 * Custom 0-9 keypad grid for digit input
 * Disables already used digits when allowRepeats is false
 */
@Composable
fun CustomKeypad(
    usedDigits: Set<String>,
    onDigitClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1: 0-4
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (0..4).forEach { digit ->
                val digitStr = digit.toString()
                KeypadButton(
                    digit = digitStr,
                    enabled = digitStr !in usedDigits,
                    onClick = { onDigitClick(digitStr) }
                )
            }
        }

        // Row 2: 5-9
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (5..9).forEach { digit ->
                val digitStr = digit.toString()
                KeypadButton(
                    digit = digitStr,
                    enabled = digitStr !in usedDigits,
                    onClick = { onDigitClick(digitStr) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomKeypadPreview() {
    BullsAndCowsTheme {
        CustomKeypad(
            usedDigits = setOf("1", "2", "5"),
            onDigitClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomKeypadEmptyPreview() {
    BullsAndCowsTheme {
        CustomKeypad(
            usedDigits = emptySet(),
            onDigitClick = {}
        )
    }
}
