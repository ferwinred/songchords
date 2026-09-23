package com.example.songchords

import com.example.songchords.model.Song
import com.example.songchords.repository.LocalSongRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SongRepositoryTest {

    private lateinit var repository: LocalSongRepository

    @Before
    fun setUp() {
        repository = LocalSongRepository()
    }

    @Test
    fun testGetInitialSongs() = runBlocking {
        val songs = repository.getSongs().first()
        assertTrue(songs.size >= 6)
        val titles = songs.map { it.title }
        assertTrue(titles.contains("Tu Poeta"))
        assertTrue(titles.contains("Renuévame"))
        assertTrue(titles.contains("La Bondad de Dios"))
        assertTrue(titles.contains("Cuan Grande Es Él"))
        assertTrue(titles.contains("Way Maker (Es El Que Abre Caminos)"))
        assertTrue(titles.contains("Sublime Gracia (Amazing Grace)"))
    }

    @Test
    fun testSearchSongs() = runBlocking {
        val results = repository.searchSongs("Witt")
        assertEquals(1, results.size)
        assertEquals("Renuévame", results[0].title)

        val spanishResults = repository.searchSongs("Spanish")
        assertTrue(spanishResults.isNotEmpty())
    }

    @Test
    fun testFavoriteToggle() = runBlocking {
        val song = repository.getSongs().first()[0]
        val initialFav = song.isFavorite

        repository.toggleFavorite(song.id)
        val updatedSong = repository.getSongById(song.id)
        assertNotNull(updatedSong)
        assertEquals(!initialFav, updatedSong!!.isFavorite)
    }

    @Test
    fun testCrudOperations() = runBlocking {
        val newSong = Song(
            id = "test_custom_1",
            title = "Test Song",
            artist = "Test Artist",
            originalKey = "D",
            content = "[D] Hello [G] World"
        )

        repository.addSong(newSong)
        val fetched = repository.getSongById("test_custom_1")
        assertNotNull(fetched)
        assertEquals("Test Song", fetched!!.title)

        val updated = fetched.copy(title = "Updated Title")
        repository.updateSong(updated)

        val reFetched = repository.getSongById("test_custom_1")
        assertEquals("Updated Title", reFetched!!.title)

        repository.deleteSong("test_custom_1")
        val deleted = repository.getSongById("test_custom_1")
        assertNull(deleted)
    }
}
