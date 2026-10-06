package com.example.songchords

import com.example.songchords.repository.FirestoreChordLibraryRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FirestoreChordLibraryRepositoryTest {

    private lateinit var repository: FirestoreChordLibraryRepository

    @Before
    fun setUp() {
        repository = FirestoreChordLibraryRepository()
    }

    @Test
    fun testOfflineCacheInitialization() {
        assertEquals(120, repository.getCacheSize())
    }

    @Test
    fun testGetChordDiagrams_guitarInstrument() {
        val result = repository.getChordDiagrams("guitar", "C", "")
        assertEquals("C", result.rootNote)
        assertEquals("", result.quality)
        assertEquals("C", result.chordSymbol)
        assertNotNull(result.guitarDiagram)
        assertNull(result.pianoNotes)
        assertEquals(listOf(-1, 3, 2, 0, 1, 0), result.guitarDiagram!!.frets)
    }

    @Test
    fun testGetChordDiagrams_pianoInstrument() {
        val result = repository.getChordDiagrams("piano", "G", "7")
        assertEquals("G", result.rootNote)
        assertEquals("7", result.quality)
        assertEquals("G7", result.chordSymbol)
        assertNull(result.guitarDiagram)
        assertNotNull(result.pianoNotes)
        assertTrue(result.pianoNotes!!.pitchClasses.contains(7)) // G
    }

    @Test
    fun testGetChordDiagrams_bothInstruments() {
        val result = repository.getChordDiagrams("both", "A", "m")
        assertEquals("A", result.rootNote)
        assertEquals("m", result.quality)
        assertEquals("Am", result.chordSymbol)
        assertNotNull(result.guitarDiagram)
        assertNotNull(result.pianoNotes)
    }

    @Test
    fun testSyncChordLibraryToFirestore() = runBlocking {
        assertEquals(0, repository.getFirestoreCollection().size)
        val success = repository.syncChordLibraryToFirestore()
        assertTrue(success)
        assertEquals(120, repository.getFirestoreCollection().size)
        val sampleDoc = repository.getFirestoreCollection()["C_major"]
        assertNotNull(sampleDoc)
        assertEquals("C", sampleDoc!!.rootNote)
        assertEquals("", sampleDoc.quality)
    }
}
