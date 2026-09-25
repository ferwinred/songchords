package com.example.songchords.ui.components

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.os.LocaleListCompat
import com.example.songchords.R
import java.util.Locale

@Composable
fun LanguageSelector(
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current

    val currentLanguage = remember(configuration) {
        val locales = AppCompatDelegate.getApplicationLocales()
        val tag = if (!locales.isEmpty) {
            locales[0]?.language ?: Locale.getDefault().language
        } else {
            Locale.getDefault().language
        }
        if (tag.startsWith("es", ignoreCase = true)) "es" else "en"
    }

    Box(modifier = modifier) {
        IconButton(
            onClick = { expanded = true }
        ) {
            Icon(
                imageVector = Icons.Rounded.Language,
                contentDescription = stringResource(R.string.language_selector),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.language_english),
                        fontWeight = if (currentLanguage == "en") FontWeight.Bold else FontWeight.Normal
                    )
                },
                onClick = {
                    expanded = false
                    setAppLocale("en")
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.language_spanish),
                        fontWeight = if (currentLanguage == "es") FontWeight.Bold else FontWeight.Normal
                    )
                },
                onClick = {
                    expanded = false
                    setAppLocale("es")
                }
            )
        }
    }
}

private fun setAppLocale(languageCode: String) {
    val localeList = LocaleListCompat.forLanguageTags(languageCode)
    AppCompatDelegate.setApplicationLocales(localeList)
}
