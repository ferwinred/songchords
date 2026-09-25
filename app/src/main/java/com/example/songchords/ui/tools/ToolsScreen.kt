package com.example.songchords.ui.tools

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.songchords.R
import com.example.songchords.ui.chordlibrary.ChordLibraryScreen
import com.example.songchords.ui.theme.SongChordsTheme
import com.example.songchords.ui.tuner.TunerScreen

enum class ToolsTab {
    TUNER,
    CHORD_LIBRARY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    initialTab: ToolsTab = ToolsTab.TUNER
) {
    var selectedTab by remember { mutableIntStateOf(initialTab.ordinal) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.tools_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PrimaryTabRow(
                selectedTabIndex = selectedTab
            ) {
                Tab(
                    selected = selectedTab == ToolsTab.TUNER.ordinal,
                    onClick = { selectedTab = ToolsTab.TUNER.ordinal },
                    text = {
                        Text(
                            text = stringResource(R.string.tab_tuner),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
                Tab(
                    selected = selectedTab == ToolsTab.CHORD_LIBRARY.ordinal,
                    onClick = { selectedTab = ToolsTab.CHORD_LIBRARY.ordinal },
                    text = {
                        Text(
                            text = stringResource(R.string.tab_chord_library),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }

            when (selectedTab) {
                ToolsTab.TUNER.ordinal -> TunerScreen()
                ToolsTab.CHORD_LIBRARY.ordinal -> ChordLibraryScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ToolsScreenPreview() {
    SongChordsTheme {
        ToolsScreen(onNavigateBack = {})
    }
}
