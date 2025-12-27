package com.example.cowsnbulls.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cowsnbulls.ui.theme.BullsAndCowsTheme

/**
 * Single keypad button for digit input
 */
@Composable
fun KeypadButton(
    digit: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(64.dp)
            .scale(if (enabled) 1f else 0.95f)
            .background(
                color = if (enabled) Color.White else Color.LightGray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 2.dp,
                color = if (enabled) Color.LightGray else Color.LightGray.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = enabled) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (enabled) Color.Black else Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun KeypadButtonEnabledPreview() {
    BullsAndCowsTheme {
        KeypadButton(
            digit = "5",
            enabled = true,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun KeypadButtonDisabledPreview() {
    BullsAndCowsTheme {
        KeypadButton(
            digit = "3",
            enabled = false,
            onClick = {}
        )
    }
}
