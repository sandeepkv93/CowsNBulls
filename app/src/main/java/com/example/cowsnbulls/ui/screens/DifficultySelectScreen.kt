package com.example.cowsnbulls.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cowsnbulls.domain.model.Difficulty
import com.example.cowsnbulls.domain.model.GameSettings
import com.example.cowsnbulls.ui.components.ModeCard
import com.example.cowsnbulls.ui.theme.BullsAndCowsTheme
import com.example.cowsnbulls.ui.theme.Purple40
import com.example.cowsnbulls.ui.theme.Purple80

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DifficultySelectScreen(
    onDifficultySelected: (Difficulty, GameSettings) -> Unit = { _, _ -> },
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose Difficulty") },
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
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Purple80.copy(alpha = 0.1f),
                            Color.White
                        )
                    )
                )
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Select your challenge level",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Easy
            ModeCard(
                icon = "😊",
                title = "Easy",
                description = "Perfect for beginners",
                onClick = {
                    onDifficultySelected(Difficulty.EASY, GameSettings())
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Medium
            ModeCard(
                icon = "😐",
                title = "Medium",
                description = "Balanced challenge",
                onClick = {
                    onDifficultySelected(Difficulty.MEDIUM, GameSettings())
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Hard
            ModeCard(
                icon = "😈",
                title = "Hard",
                description = "Expert AI awaits",
                onClick = {
                    onDifficultySelected(Difficulty.HARD, GameSettings())
                },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Default: 4 digits, no repeats, no leading zero",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DifficultySelectScreenPreview() {
    BullsAndCowsTheme {
        DifficultySelectScreen()
    }
}
