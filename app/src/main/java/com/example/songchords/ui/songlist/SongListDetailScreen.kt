package com.example.songchords.ui.songlist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.songchords.R
import com.example.songchords.repository.SampleSongs
import com.example.songchords.ui.components.LanguageSelector
import com.example.songchords.ui.components.ThemeToggleIconButton
import com.example.songchords.ui.songlist.components.SongDetailPane
import com.example.songchords.ui.theme.SongChordsTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SongListDetailScreen(
    viewModel: SongListViewModel,
    onOpenEditor: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigator = rememberListDetailPaneScaffoldNavigator<String>()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.top_bar_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    ThemeToggleIconButton()
                    LanguageSelector()
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        },
        floatingActionButton = {
            val isSinglePaneDetail = navigator.currentDestination?.pane == ListDetailPaneScaffoldRole.Detail &&
                    navigator.scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Hidden

            if (!isSinglePaneDetail) {
                ExtendedFloatingActionButton(
                    onClick = { onOpenEditor(null) },
                    icon = { Icon(imageVector = Icons.Rounded.Add, contentDescription = null) },
                    text = { Text(stringResource(R.string.new_song)) },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    ) { innerPadding ->
        ListDetailPaneScaffold(
            directive = navigator.scaffoldDirective,
            value = navigator.scaffoldValue,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            listPane = {
                AnimatedPane {
                    SongListScreenContent(
                        uiState = uiState,
                        onSearchQueryChange = viewModel::onSearchQueryChange,
                        onClearSearchQuery = { viewModel.onSearchQueryChange("") },
                        onToggleFavoritesFilter = viewModel::onToggleFavoritesFilter,
                        onKeyFilterSelect = viewModel::onKeyFilterSelect,
                        onArtistFilterSelect = viewModel::onArtistFilterSelect,
                        onTagFilterSelect = viewModel::onTagFilterSelect,
                        onClearFilters = viewModel::onClearFilters,
                        onSelectSong = { songId ->
                            viewModel.onSelectSong(songId)
                            coroutineScope.launch {
                                navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, songId)
                            }
                        },
                        onToggleFavorite = viewModel::onToggleFavorite
                    )
                }
            },
            detailPane = {
                AnimatedPane {
                    val currentSongId = navigator.currentDestination?.contentKey ?: uiState.selectedSongId
                    val currentSong = uiState.songs.firstOrNull { it.id == currentSongId } ?: uiState.selectedSong

                    SongDetailPane(
                        song = currentSong,
                        onToggleFavorite = viewModel::onToggleFavorite,
                        onEditSongClick = { songId -> onOpenEditor(songId) },
                        onBackClick = if (navigator.canNavigateBack()) {
                            {
                                coroutineScope.launch {
                                    navigator.navigateBack()
                                }
                            }
                        } else null
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 600)
@Composable
fun SongListDetailScreenTabletPreview() {
    SongChordsTheme {
        SongListScreenContent(
            uiState = SongListUiState(
                songs = SampleSongs.ALL_SONGS,
                availableKeys = listOf("G", "D", "A", "E"),
                availableArtists = listOf("Alex Campos", "Marcos Witt"),
                availableTags = listOf("Alabanza", "Adoración", "Worship")
            ),
            onSearchQueryChange = {},
            onClearSearchQuery = {},
            onToggleFavoritesFilter = {},
            onKeyFilterSelect = {},
            onArtistFilterSelect = {},
            onTagFilterSelect = {},
            onClearFilters = {},
            onSelectSong = {},
            onToggleFavorite = {}
        )
    }
}
