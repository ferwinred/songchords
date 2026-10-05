package com.example.songchords.repository

import com.example.songchords.model.Setlist
import com.example.songchords.model.Song
import kotlinx.coroutines.flow.Flow

interface SetlistRepository {
    fun getSetlists(): Flow<List<Setlist>>
    suspend fun getSetlistById(id: String): Setlist?
    suspend fun createSetlist(setlist: Setlist)
    suspend fun addSongToSetlist(setlistId: String, song: Song, transposedKey: String)
    suspend fun removeSongFromSetlist(setlistId: String, songId: String)
    suspend fun updateSetlistSongKey(setlistId: String, songId: String, newTransposedKey: String)
    suspend fun deleteSetlist(id: String)
}
