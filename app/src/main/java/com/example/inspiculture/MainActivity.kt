package com.example.inspiculture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.inspiculture.ui.theme.InspiCultureTheme
import com.example.inspiculture.BooksScreen.BooksScreen
import com.example.inspiculture.HomeScreen.HomeScreen
import com.example.inspiculture.MusicScreen.MusicScreen
import com.example.inspiculture.SettingsScreen.SettingsScreen
import com.example.inspiculture.ShowsScreen.ShowsScreen



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InspiCultureTheme {
                var selectedTab by remember { mutableStateOf(0) }  // Declare selectedTab state

                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        // Display different screens based on selectedTab
                        when (selectedTab) {
                            0 -> HomeScreen()
                            1 -> BooksScreen()
                            2 -> ShowsScreen()
                            3 -> MusicScreen()
                            4 -> SettingsScreen()
                        }
                    }
                    // Bottom navigation bar
                    EnhancedTabNavigation(
                        selectedTab = selectedTab,
                        onTabSelected = { index -> selectedTab = index }
                    )
                }
            }
        }
    }
}


