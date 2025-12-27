package com.example.cowsnbulls

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cowsnbulls.ui.navigation.AppNavigation
import com.example.cowsnbulls.ui.theme.BullsAndCowsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BullsAndCowsTheme {
                AppNavigation()
            }
        }
    }
}
