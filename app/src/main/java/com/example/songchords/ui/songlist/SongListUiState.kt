package com.example.songchords.ui.songlist

import com.example.songchords.model.Song

/**
 * State representing the UI for Song List & Search screen.
 */
data class SongListUiState(
    val searchQuery: String = "",
    val selectedArtistFilter: String? = null,
    val selectedKeyFilter: String? = null,
    val selectedTagFilter: String? = null,
    val showOnlyFavorites: Boolean = false,
    val songs: List<Song> = emptyList(),
    val availableArtists: List<String> = emptyList(),
    val availableKeys: List<String> = emptyList(),
    val availableTags: List<String> = emptyList(),
    val selectedSongId: String? = null,
    val selectedSong: Song? = null,
    val isLoading: Boolean = false
) {
    val isAnyFilterActive: Boolean
        get() = searchQuery.isNotBlank() ||
                selectedArtistFilter != null ||
                selectedKeyFilter != null ||
                selectedTagFilter != null ||
                showOnlyFavorites
}
