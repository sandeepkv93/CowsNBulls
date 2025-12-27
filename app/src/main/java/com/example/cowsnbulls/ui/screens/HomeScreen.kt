package com.example.cowsnbulls.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.cowsnbulls.ui.components.ModeCard
import com.example.cowsnbulls.ui.theme.BullsAndCowsTheme
import com.example.cowsnbulls.ui.theme.Purple40
import com.example.cowsnbulls.ui.theme.Purple80

@Composable
fun HomeScreen(
    onVsComputerClick: () -> Unit = {},
    onPlayWithFriendClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
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
        // Title
        Text(
            text = "🐂🐮",
            fontSize = 80.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Bulls & Cows",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 42.sp
        )
        Text(
            text = "Crack the code, win the game!",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        // Mode Cards
        ModeCard(
            icon = "🤖",
            title = "VS Computer",
            description = "Challenge the AI",
            onClick = onVsComputerClick,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        ModeCard(
            icon = "👥",
            title = "Play with Friend",
            description = "Battle remotely (Coming Soon)",
            onClick = onPlayWithFriendClick
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Footer
        Text(
            text = "Select a mode to begin",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    BullsAndCowsTheme {
        HomeScreen()
    }
}
