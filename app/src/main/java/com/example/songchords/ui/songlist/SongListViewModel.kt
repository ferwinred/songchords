package com.example.songchords.ui.songlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.songchords.model.Song
import com.example.songchords.repository.CloudSyncRepository
import com.example.songchords.repository.LocalSongRepository
import com.example.songchords.repository.SongRepository
import com.example.songchords.repository.SyncStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private data class FilterState(
    val query: String = "",
    val artist: String? = null,
    val key: String? = null,
    val tag: String? = null,
    val favoritesOnly: Boolean = false,
    val selectedSongId: String? = null
)

class SongListViewModel(
    private val repository: SongRepository = LocalSongRepository()
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val selectedArtist = MutableStateFlow<String?>(null)
    private val selectedKey = MutableStateFlow<String?>(null)
    private val selectedTag = MutableStateFlow<String?>(null)
    private val showOnlyFavorites = MutableStateFlow(false)
    private val selectedSongId = MutableStateFlow<String?>(null)

    private val syncStatusFlow: StateFlow<SyncStatus> = (repository as? CloudSyncRepository)?.syncStatus
        ?: MutableStateFlow(SyncStatus.IDLE).asStateFlow()

    private val lastSyncedAtFlow: StateFlow<Long?> = (repository as? CloudSyncRepository)?.lastSyncedAt
        ?: MutableStateFlow(null).asStateFlow()

    private val baseFilters = combine(
        searchQuery,
        selectedArtist,
        selectedKey,
        selectedTag,
        showOnlyFavorites
    ) { query, artist, key, tag, fav ->
        FilterState(
            query = query,
            artist = artist,
            key = key,
            tag = tag,
            favoritesOnly = fav
        )
    }

    private val filterState = combine(
        baseFilters,
        selectedSongId
    ) { filters, songId ->
        filters.copy(selectedSongId = songId)
    }

    val uiState: StateFlow<SongListUiState> = combine(
        repository.getSongs(),
        filterState,
        syncStatusFlow,
        lastSyncedAtFlow
    ) { allSongs: List<Song>, filters: FilterState, syncStatus: SyncStatus, lastSyncedAt: Long? ->

        val availableArtists = allSongs.map { it.artist }.distinct().sorted()
        val availableKeys = allSongs.map { it.originalKey }.distinct().sorted()
        val availableTags = allSongs.flatMap { it.tags }.distinct().sorted()

        val filteredSongs = allSongs.filter { song ->
            // Search query matching
            val matchesQuery = if (filters.query.isBlank()) {
                true
            } else {
                val q = filters.query.trim()
                song.title.contains(q, ignoreCase = true) ||
                song.artist.contains(q, ignoreCase = true) ||
                song.originalKey.contains(q, ignoreCase = true) ||
                song.tags.any { it.contains(q, ignoreCase = true) } ||
                song.content.contains(q, ignoreCase = true)
            }

            // Artist filter matching
            val matchesArtist = filters.artist == null ||
                    song.artist.equals(filters.artist, ignoreCase = true)

            // Key filter matching
            val matchesKey = filters.key == null ||
                    song.originalKey.equals(filters.key, ignoreCase = true)

            // Tag filter matching
            val matchesTag = filters.tag == null ||
                    song.tags.any { it.equals(filters.tag, ignoreCase = true) }

            // Favorite filter matching
            val matchesFav = !filters.favoritesOnly || song.isFavorite

            matchesQuery && matchesArtist && matchesKey && matchesTag && matchesFav
        }

        val currentSelectedSong = filters.selectedSongId?.let { id ->
            allSongs.firstOrNull { it.id == id }
        } ?: filteredSongs.firstOrNull()

        SongListUiState(
            searchQuery = filters.query,
            selectedArtistFilter = filters.artist,
            selectedKeyFilter = filters.key,
            selectedTagFilter = filters.tag,
            showOnlyFavorites = filters.favoritesOnly,
            songs = filteredSongs,
            availableArtists = availableArtists,
            availableKeys = availableKeys,
            availableTags = availableTags,
            selectedSongId = currentSelectedSong?.id ?: filters.selectedSongId,
            selectedSong = currentSelectedSong,
            isLoading = false,
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SongListUiState(isLoading = true)
    )

    fun triggerSync(onResult: (SyncStatus) -> Unit = {}) {
        viewModelScope.launch {
            if (repository is CloudSyncRepository) {
                val status = repository.triggerSync()
                onResult(status)
            } else {
                onResult(SyncStatus.SYNCED)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun onArtistFilterSelect(artist: String?) {
        selectedArtist.value = artist
    }

    fun onKeyFilterSelect(key: String?) {
        selectedKey.value = key
    }

    fun onTagFilterSelect(tag: String?) {
        selectedTag.value = tag
    }

    fun onToggleFavoritesFilter() {
        showOnlyFavorites.value = !showOnlyFavorites.value
    }

    fun onClearFilters() {
        searchQuery.value = ""
        selectedArtist.value = null
        selectedKey.value = null
        selectedTag.value = null
        showOnlyFavorites.value = false
    }

    fun onToggleFavorite(songId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(songId)
        }
    }

    fun onSelectSong(songId: String?) {
        selectedSongId.value = songId
    }

    fun onDeleteSong(songId: String) {
        viewModelScope.launch {
            repository.deleteSong(songId)
            if (selectedSongId.value == songId) {
                selectedSongId.value = null
            }
        }
    }

    fun duplicateSong(song: Song, onComplete: ((Song) -> Unit)? = null) {
        viewModelScope.launch {
            val currentUserId = com.example.songchords.auth.UserIdentityManager.currentUserId
            val currentUserName = com.example.songchords.auth.UserIdentityManager.currentUserName
            val duplicatedSong = song.cloneForUser(currentUserId, currentUserName)
            repository.addSong(duplicatedSong)
            selectedSongId.value = duplicatedSong.id
            onComplete?.invoke(duplicatedSong)
        }
    }

    fun importSongFromUri(context: android.content.Context, uri: android.net.Uri, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val importedSong = com.example.songchords.utils.SongJsonUtils.importSongFromUri(context, uri)
                if (importedSong != null) {
                    repository.addSong(importedSong)
                    selectedSongId.value = importedSong.id
                    onResult(true, importedSong.title)
                } else {
                    onResult(false, "")
                }
            } catch (e: Exception) {
                onResult(false, e.message ?: "")
            }
        }
    }

    companion object {
        fun Factory(repository: SongRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SongListViewModel(repository) as T
                }
            }
    }
}
