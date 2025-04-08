package com.example.inspiculture.data

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

class ThemePreferences(private val context: Context) {
    private val dataStore = context.dataStore
    private val THEME_KEY = stringPreferencesKey("theme_mode")

    val themeMode: Flow<ThemeMode> = dataStore.data.map { preferences ->
        ThemeMode.valueOf(preferences[THEME_KEY] ?: ThemeMode.SYSTEM.name)
    }
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("theme_preferences", Context.MODE_PRIVATE)

    fun isDarkMode(): Boolean {
        // Retrieve the saved theme mode from shared preferences
        return sharedPreferences.getBoolean("dark_mode", false)
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = themeMode.name
        }
    }
}