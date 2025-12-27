package com.example.cowsnbulls.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cowsnbulls.ui.theme.BullsAndCowsTheme
import com.example.cowsnbulls.ui.theme.BullsGreen40
import com.example.cowsnbulls.ui.theme.CowsAmber40

/**
 * Feedback indicator pill showing bulls or cows count
 */
@Composable
fun FeedbackPill(
    icon: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = icon,
            fontSize = 18.sp
        )
        Text(
            text = count.toString(),
            fontWeight = FontWeight.Bold,
            color = color,
            fontSize = 16.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FeedbackPillBullsPreview() {
    BullsAndCowsTheme {
        FeedbackPill(
            icon = "🐂",
            count = 2,
            color = BullsGreen40
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FeedbackPillCowsPreview() {
    BullsAndCowsTheme {
        FeedbackPill(
            icon = "🐮",
            count = 1,
            color = CowsAmber40
        )
    }
}
