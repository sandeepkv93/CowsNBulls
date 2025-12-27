package com.example.cowsnbulls.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cowsnbulls.ui.theme.BullsAndCowsTheme
import com.example.cowsnbulls.ui.theme.Purple40
import com.example.cowsnbulls.ui.theme.Purple80

/**
 * Single digit display slot
 * Shows a digit with gradient background when filled, or "?" when empty
 */
@Composable
fun DigitSlot(
    digit: String?,
    filled: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (filled) {
        Brush.verticalGradient(
            colors = listOf(Purple80, Purple40)
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(Color.White, Color.White)
        )
    }

    val textColor = if (filled) Color.White else Color.Gray

    Box(
        modifier = modifier
            .size(48.dp, 56.dp)
            .background(
                brush = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 2.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (filled && digit != null) digit else "?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DigitSlotPreview() {
    BullsAndCowsTheme {
        DigitSlot(digit = "5", filled = true)
    }
}

@Preview(showBackground = true)
@Composable
fun DigitSlotEmptyPreview() {
    BullsAndCowsTheme {
        DigitSlot(digit = null, filled = false)
    }
}
