package com.example.songchords

import com.example.songchords.engine.TranspositionEngine
import com.example.songchords.model.Setlist
import com.example.songchords.model.Song
import com.example.songchords.repository.LocalSetlistRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SetlistRepositoryTest {

    private lateinit var repository: LocalSetlistRepository

    @Before
    fun setUp() {
        repository = LocalSetlistRepository()
    }

    @Test
    fun testGetInitialSetlists() = runBlocking {
        val setlists = repository.getSetlists().first()
        assertTrue(setlists.size >= 2)
        val titles = setlists.map { it.title }
        assertTrue(titles.contains("Servicio de Domingo - Adoración"))
        assertTrue(titles.contains("Ensayo Grupo de Alabanza"))
    }

    @Test
    fun testCreateAndGetSetlist() = runBlocking {
        val newSetlist = Setlist(
            id = "test-setlist-1",
            title = "Concierto Juvenil",
            description = "Canciones para el evento de jóvenes",
            createdByUserId = "user-123",
            createdByName = "Marcos"
        )

        repository.createSetlist(newSetlist)

        val setlists = repository.getSetlists().first()
        val fetched = repository.getSetlistById("test-setlist-1")

        assertNotNull(fetched)
        assertEquals("Concierto Juvenil", fetched!!.title)
        assertEquals("Marcos", fetched.createdByName)
        assertTrue(setlists.any { it.id == "test-setlist-1" })
    }

    @Test
    fun testAddSongToSetlistWithKeyOverride() = runBlocking {
        val testSong = Song(
            id = "song-test-1",
            title = "Way Maker",
            artist = "Sinach",
            originalKey = "E",
            content = "[E] Way Maker"
        )

        val setlistId = "setlist-sunday-1"
        repository.addSongToSetlist(
            setlistId = setlistId,
            song = testSong,
            transposedKey = "G"
        )

        val updatedSetlist = repository.getSetlistById(setlistId)
        assertNotNull(updatedSetlist)

        val addedSong = updatedSetlist!!.songs.firstOrNull { it.songId == "song-test-1" }
        assertNotNull(addedSong)
        assertEquals("Way Maker", addedSong!!.songTitle)
        assertEquals("E", addedSong.originalKey)
        assertEquals("G", addedSong.transposedKey)
    }

    @Test
    fun testUpdateSetlistSongKey() = runBlocking {
        val setlistId = "setlist-sunday-1"
        val sundaySetlist = repository.getSetlistById(setlistId)
        assertNotNull(sundaySetlist)
        assertTrue(sundaySetlist!!.songs.isNotEmpty())

        val songToUpdate = sundaySetlist.songs.first()
        val songId = songToUpdate.songId

        repository.updateSetlistSongKey(
            setlistId = setlistId,
            songId = songId,
            newTransposedKey = "F"
        )

        val reFetched = repository.getSetlistById(setlistId)
        val updatedSong = reFetched!!.songs.first { it.songId == songId }
        assertEquals("F", updatedSong.transposedKey)
    }

    @Test
    fun testRemoveSongFromSetlist() = runBlocking {
        val setlistId = "setlist-sunday-1"
        val setlist = repository.getSetlistById(setlistId)
        assertNotNull(setlist)
        val initialCount = setlist!!.songs.size
        assertTrue(initialCount > 0)

        val songIdToRemove = setlist.songs.first().songId
        repository.removeSongFromSetlist(setlistId, songIdToRemove)

        val updated = repository.getSetlistById(setlistId)
        assertEquals(initialCount - 1, updated!!.songs.size)
        assertTrue(updated.songs.none { it.songId == songIdToRemove })
    }

    @Test
    fun testDeleteSetlist() = runBlocking {
        val setlistId = "setlist-rehearsal-1"
        assertNotNull(repository.getSetlistById(setlistId))

        repository.deleteSetlist(setlistId)

        assertNull(repository.getSetlistById(setlistId))
        val allSetlists = repository.getSetlists().first()
        assertTrue(allSetlists.none { it.id == setlistId })
    }

    @Test
    fun testKeyTranspositionDeltaCalculation() {
        // G to C -> +5 semitones
        val deltaGToC = TranspositionEngine.calculateSemitonesDelta("G", "C")
        assertEquals(5, deltaGToC)

        // G to G -> 0 semitones
        val deltaGToG = TranspositionEngine.calculateSemitonesDelta("G", "G")
        assertEquals(0, deltaGToG)

        // C to G -> -5 semitones
        val deltaCToG = TranspositionEngine.calculateSemitonesDelta("C", "G")
        assertEquals(-5, deltaCToG)

        // D to E -> +2 semitones
        val deltaDToE = TranspositionEngine.calculateSemitonesDelta("D", "E")
        assertEquals(2, deltaDToE)
    }
}
