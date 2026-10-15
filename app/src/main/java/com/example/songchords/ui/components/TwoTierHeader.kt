package com.example.songchords.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import com.example.songchords.R
import com.example.songchords.repository.SyncStatus
import com.example.songchords.ui.theme.LocalOnToggleThemeMode
import com.example.songchords.ui.theme.LocalThemeMode
import com.example.songchords.ui.theme.ThemeMode

@Composable
fun TwoTierHeader(
    modifier: Modifier = Modifier,
    titleText: String = stringResource(R.string.app_name),
    navigationIcon: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    syncStatus: SyncStatus = SyncStatus.IDLE,
    onCloudSyncClick: () -> Unit = {},
    onSubscriptionClick: () -> Unit = {},
    onImportJsonClick: () -> Unit = {},
    themeMode: ThemeMode = LocalThemeMode.current,
    onToggleTheme: () -> Unit = LocalOnToggleThemeMode.current,
) {
    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    // Brand Dark Navy background: #020873 Light / #1E2558 Dark
    val brandDarkNavy = if (isDark) Color(0xFF1E2558) else Color(0xFF020873)
    var showPrivacyPolicy by remember { mutableStateOf(false) }

    Surface(
        color = brandDarkNavy,
        contentColor = Color.White,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
        ) {
            // Level 1: Title Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false),
                ) {
                    navigationIcon?.invoke()
                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                trailingContent?.invoke()
            }

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.15f),
                thickness = 1.dp,
            )

            // Level 2: Action Tools Bar directly below Title Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CloudSyncButton(
                    onClick = onCloudSyncClick,
                    syncStatus = syncStatus,
                    tint = Color.White,
                )
                SubscriptionButton(
                    onClick = onSubscriptionClick,
                    tint = Color.White,
                )
                ImportJsonButton(
                    onClick = onImportJsonClick,
                    tint = Color.White,
                )
                ThemeToggleIconButton(
                    tint = Color.White,
                    themeMode = themeMode,
                    onToggleTheme = onToggleTheme,
                )
                IconButton(onClick = { showPrivacyPolicy = true }) {
                    Icon(
                        imageVector = Icons.Rounded.Policy,
                        contentDescription = stringResource(R.string.privacy_policy_title),
                        tint = Color.White
                    )
                }
                LanguageSelector(
                    tint = Color.White,
                )
            }
        }
    }

    if (showPrivacyPolicy) {
        val rawHtml = stringResource(R.string.privacy_policy_content)
        // Convert HTML breaks and bold tags to simple text formatting (since Android's AlertDialog doesn't render HTML directly out of the box in Compose Text easily without AndroidView, we strip/replace or just show it as is. A better way in simple Compose is stripping HTML tags for simple text or just showing plain text version.)
        val parsedText = remember(rawHtml) {
            HtmlCompat.fromHtml(rawHtml, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
        }

        AlertDialog(
            onDismissRequest = { showPrivacyPolicy = false },
            title = {
                Text(
                    text = stringResource(R.string.privacy_policy_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = parsedText,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.verticalScroll(rememberScrollState())
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyPolicy = false }) {
                    Text(stringResource(android.R.string.ok))
                }
            }
        )
    }
}
