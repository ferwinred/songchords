package com.example.songchords.repository

import com.example.songchords.model.Song
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    fun getSongs(): Flow<List<Song>>
    suspend fun getSongById(id: String): Song?
    suspend fun searchSongs(query: String): List<Song>
    suspend fun filterByTag(tag: String): List<Song>
    suspend fun toggleFavorite(id: String)
    suspend fun addSong(song: Song)
    suspend fun updateSong(song: Song)
    suspend fun deleteSong(id: String)
}
