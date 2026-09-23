package com.example.songchords.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.songchords.R
import com.example.songchords.ui.theme.LocalOnToggleThemeMode
import com.example.songchords.ui.theme.LocalThemeMode
import com.example.songchords.ui.theme.SongChordsTheme
import com.example.songchords.ui.theme.ThemeMode

@Composable
fun ThemeToggleIconButton(
    modifier: Modifier = Modifier,
    themeMode: ThemeMode = LocalThemeMode.current,
    onToggleTheme: () -> Unit = LocalOnToggleThemeMode.current
) {
    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    IconButton(
        onClick = onToggleTheme,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isDark) Icons.Rounded.WbSunny else Icons.Rounded.DarkMode,
            contentDescription = stringResource(R.string.theme_toggle),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview
@Composable
fun ThemeToggleIconButtonDarkPreview() {
    SongChordsTheme(themeMode = ThemeMode.DARK) {
        ThemeToggleIconButton()
    }
}

@Preview
@Composable
fun ThemeToggleIconButtonLightPreview() {
    SongChordsTheme(themeMode = ThemeMode.LIGHT) {
        ThemeToggleIconButton()
    }
}
