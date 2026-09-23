package com.example.songchords.ui.songlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.songchords.R
import com.example.songchords.repository.SampleSongs
import com.example.songchords.ui.songlist.components.EmptySongsView
import com.example.songchords.ui.songlist.components.FilterChipGroup
import com.example.songchords.ui.songlist.components.SongListItem
import com.example.songchords.ui.songlist.components.SongSearchBar
import com.example.songchords.ui.theme.SongChordsTheme

@Composable
fun SongListScreenContent(
    uiState: SongListUiState,
    onSearchQueryChange: (String) -> Unit,
    onClearSearchQuery: () -> Unit,
    onToggleFavoritesFilter: () -> Unit,
    onKeyFilterSelect: (String?) -> Unit,
    onArtistFilterSelect: (String?) -> Unit,
    onTagFilterSelect: (String?) -> Unit,
    onClearFilters: () -> Unit,
    onSelectSong: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Search Bar
        SongSearchBar(
            query = uiState.searchQuery,
            onQueryChange = onSearchQueryChange,
            onClearQuery = onClearSearchQuery
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Filter Chips Row
        FilterChipGroup(
            showOnlyFavorites = uiState.showOnlyFavorites,
            selectedKeyFilter = uiState.selectedKeyFilter,
            selectedArtistFilter = uiState.selectedArtistFilter,
            selectedTagFilter = uiState.selectedTagFilter,
            availableKeys = uiState.availableKeys,
            availableArtists = uiState.availableArtists,
            availableTags = uiState.availableTags,
            isAnyFilterActive = uiState.isAnyFilterActive,
            onToggleFavoritesFilter = onToggleFavoritesFilter,
            onKeyFilterSelect = onKeyFilterSelect,
            onArtistFilterSelect = onArtistFilterSelect,
            onTagFilterSelect = onTagFilterSelect,
            onClearFilters = onClearFilters
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Results Summary Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (uiState.songs.size == 1) {
                    stringResource(R.string.songs_found_one, 1)
                } else {
                    stringResource(R.string.songs_found_other, uiState.songs.size)
                },
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            AnimatedVisibility(
                visible = uiState.isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Song List or Empty View
        if (uiState.songs.isEmpty() && !uiState.isLoading) {
            EmptySongsView(
                isFilterActive = uiState.isAnyFilterActive,
                onClearFilters = onClearFilters,
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = uiState.songs,
                    key = { it.id }
                ) { song ->
                    SongListItem(
                        song = song,
                        isSelected = song.id == uiState.selectedSongId,
                        onClick = { onSelectSong(song.id) },
                        onToggleFavorite = { onToggleFavorite(song.id) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SongListScreenContentPreview() {
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
