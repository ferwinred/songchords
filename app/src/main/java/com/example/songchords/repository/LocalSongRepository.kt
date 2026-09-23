package com.example.songchords.repository

import com.example.songchords.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocalSongRepository(
    initialSongs: List<Song> = SampleSongs.ALL_SONGS
) : SongRepository {

    private val songsState = MutableStateFlow(initialSongs)

    override fun getSongs(): Flow<List<Song>> = songsState.asStateFlow()

    override suspend fun getSongById(id: String): Song? {
        return songsState.value.firstOrNull { it.id == id }
    }

    override suspend fun searchSongs(query: String): List<Song> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return songsState.value
        return songsState.value.filter { song ->
            song.title.contains(trimmed, ignoreCase = true) ||
            song.artist.contains(trimmed, ignoreCase = true) ||
            song.originalKey.contains(trimmed, ignoreCase = true) ||
            song.tags.any { it.contains(trimmed, ignoreCase = true) }
        }
    }

    override suspend fun filterByTag(tag: String): List<Song> {
        val trimmed = tag.trim()
        if (trimmed.isEmpty()) return songsState.value
        return songsState.value.filter { song ->
            song.tags.any { it.equals(trimmed, ignoreCase = true) }
        }
    }

    override suspend fun toggleFavorite(id: String) {
        val current = songsState.value.toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index != -1) {
            val existing = current[index]
            current[index] = existing.copy(isFavorite = !existing.isFavorite)
            songsState.value = current
        }
    }

    override suspend fun addSong(song: Song) {
        val current = songsState.value.toMutableList()
        current.add(0, song)
        songsState.value = current
    }

    override suspend fun updateSong(song: Song) {
        val current = songsState.value.toMutableList()
        val index = current.indexOfFirst { it.id == song.id }
        if (index != -1) {
            current[index] = song
            songsState.value = current
        }
    }

    override suspend fun deleteSong(id: String) {
        val current = songsState.value.toMutableList()
        current.removeAll { it.id == id }
        songsState.value = current
    }
}
