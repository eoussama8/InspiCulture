package com.example.inspiculture.SettingsScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.inspiculture.data.ThemeMode
import com.example.inspiculture.data.ThemePreferences
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(themePreferences: ThemePreferences) {
    val themeMode by themePreferences.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dark Mode",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = themeMode == ThemeMode.DARK,
                onCheckedChange = { isChecked ->
                    coroutineScope.launch {
                        themePreferences.setThemeMode(
                            if (isChecked) ThemeMode.DARK else ThemeMode.LIGHT
                        )
                    }
                }
            )
        }

        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Follow System Theme",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = themeMode == ThemeMode.SYSTEM,
                onCheckedChange = { isChecked ->
                    coroutineScope.launch {
                        themePreferences.setThemeMode(
                            if (isChecked) ThemeMode.SYSTEM else ThemeMode.LIGHT
                        )
                    }
                }
            )
        }
    }
}
