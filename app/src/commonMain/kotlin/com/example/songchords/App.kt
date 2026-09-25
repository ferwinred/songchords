package com.example.songchords

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.songchords.ui.navigation.SongChordsNavHost
import com.example.songchords.ui.theme.SongChordsTheme
import com.example.songchords.ui.theme.ThemeMode

/**
 * Main cross-platform Jetpack Compose UI entry point shared between Android and iOS.
 */
@Composable
fun App() {
    var themeMode by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
    val isSystemDark = isSystemInDarkTheme()

    SongChordsTheme(
        themeMode = themeMode,
        onToggleTheme = {
            val currentlyDark = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemDark
            }
            themeMode = if (currentlyDark) ThemeMode.LIGHT else ThemeMode.DARK
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SongChordsNavHost()
        }
    }
}
