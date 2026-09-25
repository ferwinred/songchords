package com.example.songchords

import com.example.songchords.model.Song
import com.example.songchords.utils.SongJsonUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class SongJsonUtilsTest {

    @Test
    fun testExportToJson_containsAllSongProperties() {
        val song = Song(
            id = "test-song-123",
            title = "Tu Poeta",
            artist = "Alex Campos",
            originalKey = "G",
            content = "[G]Tu poeta, [D]tu canción\n[Em]Mi supremo [C]amor",
            tempo = 120,
            timeSignature = "4/4",
            tags = listOf("Alabanza", "Adoración"),
            isFavorite = true,
            createdAt = 1700000000000L
        )

        val json = SongJsonUtils.exportToJson(song)

        assertNotNull(json)
        assertTrue(json.contains("Tu Poeta"))
        assertTrue(json.contains("Alex Campos"))
        assertTrue(json.contains("G"))
        assertTrue(json.contains("120"))
        assertTrue(json.contains("4/4"))
        assertTrue(json.contains("Alabanza"))
        assertTrue(json.contains("Adoración"))
    }

    @Test
    fun testImportFromJson_parsesValidJson() {
        val json = """
            {
              "id": "custom-id-456",
              "title": "Cuan Grande Es El",
              "artist": "Tradicional",
              "originalKey": "A",
              "content": "[A]Señor mi Dios [D]al contemplar los cielos",
              "tempo": 72,
              "timeSignature": "4/4",
              "tags": ["Himno", "Clásico"],
              "isFavorite": false,
              "createdAt": 1700000000000
            }
        """.trimIndent()

        val song = SongJsonUtils.importFromJson(json)

        assertEquals("custom-id-456", song.id)
        assertEquals("Cuan Grande Es El", song.title)
        assertEquals("Tradicional", song.artist)
        assertEquals("A", song.originalKey)
        assertEquals("[A]Señor mi Dios [D]al contemplar los cielos", song.content)
        assertEquals(72, song.tempo)
        assertEquals("4/4", song.timeSignature)
        assertEquals(listOf("Himno", "Clásico"), song.tags)
        assertEquals(false, song.isFavorite)
    }

    @Test
    fun testImportFromJson_handlesMissingOptionalFields() {
        val json = """
            {
              "title": "Grace Alone",
              "content": "[C]Grace alone [F]by faith alone"
            }
        """.trimIndent()

        val song = SongJsonUtils.importFromJson(json)

        assertEquals("Grace Alone", song.title)
        assertEquals("Unknown Artist", song.artist)
        assertEquals("C", song.originalKey)
        assertEquals("[C]Grace alone [F]by faith alone", song.content)
        assertEquals(null, song.tempo)
        assertEquals("4/4", song.timeSignature)
        assertTrue(song.tags.isEmpty())
        assertTrue(song.id.isNotBlank())
    }

    @Test
    fun testRoundTrip_exportAndImport_preservesData() {
        val originalSong = Song(
            id = UUID.randomUUID().toString(),
            title = "Way Maker",
            artist = "Sinach",
            originalKey = "E",
            content = "[E]You are here, moving in our midst\n[B]I worship You, [C#m]I worship You\n[A]You are Way Maker",
            tempo = 68,
            timeSignature = "4/4",
            tags = listOf("Worship", "Praise"),
            isFavorite = true
        )

        val exportedJson = SongJsonUtils.exportToJson(originalSong)
        val reImportedSong = SongJsonUtils.importFromJson(exportedJson)

        assertEquals(originalSong.id, reImportedSong.id)
        assertEquals(originalSong.title, reImportedSong.title)
        assertEquals(originalSong.artist, reImportedSong.artist)
        assertEquals(originalSong.originalKey, reImportedSong.originalKey)
        assertEquals(originalSong.content, reImportedSong.content)
        assertEquals(originalSong.tempo, reImportedSong.tempo)
        assertEquals(originalSong.timeSignature, reImportedSong.timeSignature)
        assertEquals(originalSong.tags, reImportedSong.tags)
        assertEquals(originalSong.isFavorite, reImportedSong.isFavorite)
    }
}
