package com.example.songchords.repository

import com.example.songchords.model.Setlist
import com.example.songchords.model.SetlistSong
import com.example.songchords.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocalSetlistRepository(
    initialSetlists: List<Setlist> = createSampleSetlists()
) : SetlistRepository {

    private val setlistsState = MutableStateFlow(initialSetlists)

    override fun getSetlists(): Flow<List<Setlist>> = setlistsState.asStateFlow()

    override suspend fun getSetlistById(id: String): Setlist? {
        return setlistsState.value.firstOrNull { it.id == id }
    }

    override suspend fun createSetlist(setlist: Setlist) {
        val current = setlistsState.value.toMutableList()
        current.add(0, setlist)
        setlistsState.value = current
    }

    override suspend fun addSongToSetlist(setlistId: String, song: Song, transposedKey: String) {
        val current = setlistsState.value.toMutableList()
        val index = current.indexOfFirst { it.id == setlistId }
        if (index != -1) {
            val setlist = current[index]
            val existingSongIndex = setlist.songs.indexOfFirst { it.songId == song.id }
            val updatedSongs = setlist.songs.toMutableList()

            val setlistSong = SetlistSong(
                songId = song.id,
                songTitle = song.title,
                artist = song.artist,
                originalKey = song.originalKey,
                transposedKey = transposedKey.ifBlank { song.originalKey }
            )

            if (existingSongIndex != -1) {
                updatedSongs[existingSongIndex] = setlistSong
            } else {
                updatedSongs.add(setlistSong)
            }

            current[index] = setlist.copy(songs = updatedSongs)
            setlistsState.value = current
        }
    }

    override suspend fun removeSongFromSetlist(setlistId: String, songId: String) {
        val current = setlistsState.value.toMutableList()
        val index = current.indexOfFirst { it.id == setlistId }
        if (index != -1) {
            val setlist = current[index]
            val updatedSongs = setlist.songs.filterNot { it.songId == songId }
            current[index] = setlist.copy(songs = updatedSongs)
            setlistsState.value = current
        }
    }

    override suspend fun updateSetlistSongKey(
        setlistId: String,
        songId: String,
        newTransposedKey: String
    ) {
        val current = setlistsState.value.toMutableList()
        val index = current.indexOfFirst { it.id == setlistId }
        if (index != -1) {
            val setlist = current[index]
            val updatedSongs = setlist.songs.map { song ->
                if (song.songId == songId) {
                    song.copy(transposedKey = newTransposedKey.ifBlank { song.originalKey })
                } else {
                    song
                }
            }
            current[index] = setlist.copy(songs = updatedSongs)
            setlistsState.value = current
        }
    }

    override suspend fun deleteSetlist(id: String) {
        val current = setlistsState.value.toMutableList()
        current.removeAll { it.id == id }
        setlistsState.value = current
    }

    companion object {
        fun createSampleSetlists(): List<Setlist> {
            val sundayServiceSongs = listOf(
                SetlistSong(
                    songId = SampleSongs.TU_POETA.id,
                    songTitle = SampleSongs.TU_POETA.title,
                    artist = SampleSongs.TU_POETA.artist,
                    originalKey = SampleSongs.TU_POETA.originalKey,
                    transposedKey = "C"
                ),
                SetlistSong(
                    songId = SampleSongs.CUAN_GRANDE_ES_EL.id,
                    songTitle = SampleSongs.CUAN_GRANDE_ES_EL.title,
                    artist = SampleSongs.CUAN_GRANDE_ES_EL.artist,
                    originalKey = SampleSongs.CUAN_GRANDE_ES_EL.originalKey,
                    transposedKey = "A"
                ),
                SetlistSong(
                    songId = SampleSongs.RENUEVAME.id,
                    songTitle = SampleSongs.RENUEVAME.title,
                    artist = SampleSongs.RENUEVAME.artist,
                    originalKey = SampleSongs.RENUEVAME.originalKey,
                    transposedKey = "D"
                )
            )

            val rehearsalSongs = listOf(
                SetlistSong(
                    songId = SampleSongs.LA_BONDAD_DE_DIOS.id,
                    songTitle = SampleSongs.LA_BONDAD_DE_DIOS.title,
                    artist = SampleSongs.LA_BONDAD_DE_DIOS.artist,
                    originalKey = SampleSongs.LA_BONDAD_DE_DIOS.originalKey,
                    transposedKey = "G"
                ),
                SetlistSong(
                    songId = SampleSongs.WAY_MAKER.id,
                    songTitle = SampleSongs.WAY_MAKER.title,
                    artist = SampleSongs.WAY_MAKER.artist,
                    originalKey = SampleSongs.WAY_MAKER.originalKey,
                    transposedKey = "E"
                )
            )

            return listOf(
                Setlist(
                    id = "setlist-sunday-1",
                    title = "Servicio de Domingo - Adoración",
                    description = "Lista principal para el servicio de adoración dominical",
                    createdByUserId = "system-admin",
                    createdByName = "Líder de Alabanza",
                    songs = sundayServiceSongs
                ),
                Setlist(
                    id = "setlist-rehearsal-1",
                    title = "Ensayo Grupo de Alabanza",
                    description = "Lista de ensayo y práctica semanal",
                    createdByUserId = "system-admin",
                    createdByName = "Grupo de Alabanza",
                    songs = rehearsalSongs
                )
            )
        }
    }
}
