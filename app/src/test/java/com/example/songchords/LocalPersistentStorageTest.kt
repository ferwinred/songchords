package com.example.songchords

import com.example.songchords.model.Setlist
import com.example.songchords.model.SetlistSong
import com.example.songchords.model.Song
import com.example.songchords.repository.LocalSetlistRepository
import com.example.songchords.repository.LocalSongRepository
import com.example.songchords.repository.SampleSongs
import com.example.songchords.repository.local.LocalPersistentStorage
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class LocalPersistentStorageTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var filesDir: File
    private lateinit var storage: LocalPersistentStorage

    @Before
    fun setUp() {
        filesDir = tempFolder.newFolder("filesDir")
        storage = LocalPersistentStorage(filesDir)
    }

    @Test
    fun testInitialRun_populatesSampleSongsAndSetlists() {
        val songsFile = File(filesDir, LocalPersistentStorage.SONGS_FILE_NAME)
        val setlistsFile = File(filesDir, LocalPersistentStorage.SETLISTS_FILE_NAME)

        assertTrue(!songsFile.exists())
        assertTrue(!setlistsFile.exists())

        val songs = storage.loadLocalSongs()
        assertTrue(songs.isNotEmpty())
        assertEquals(SampleSongs.ALL_SONGS.size, songs.size)
        assertTrue(songsFile.exists())
        assertTrue(songsFile.length() > 0)

        val setlists = storage.loadLocalSetlists()
        assertTrue(setlists.isNotEmpty())
        assertEquals(LocalSetlistRepository.createSampleSetlists().size, setlists.size)
        assertTrue(setlistsFile.exists())
        assertTrue(setlistsFile.length() > 0)
    }

    @Test
    fun testSaveAndLoadSongs_jsonRoundtrip() {
        val customSongs = listOf(
            Song(
                id = "persistent_song_1",
                title = "Persistent Anthem",
                artist = "Storage Band",
                originalKey = "F#",
                content = "[F#] Glory [B] Hallelujah",
                comments = "Test comment",
                tempo = 120,
                timeSignature = "3/4",
                tags = listOf("Test", "Persistent", "Rock"),
                isFavorite = true,
                createdAt = 1234567890L,
                createdByUserId = "user_42",
                createdByName = "Tester"
            ),
            Song(
                id = "persistent_song_2",
                title = "Second Song",
                artist = "Solo Artist",
                originalKey = "Bb",
                content = "[Bb] Smooth chords"
            )
        )

        storage.saveLocalSongs(customSongs)

        val reloadedStorage = LocalPersistentStorage(filesDir)
        val loadedSongs = reloadedStorage.loadLocalSongs()

        assertEquals(2, loadedSongs.size)

        val firstSong = loadedSongs.first { it.id == "persistent_song_1" }
        assertEquals("Persistent Anthem", firstSong.title)
        assertEquals("Storage Band", firstSong.artist)
        assertEquals("F#", firstSong.originalKey)
        assertEquals("[F#] Glory [B] Hallelujah", firstSong.content)
        assertEquals("Test comment", firstSong.comments)
        assertEquals(120, firstSong.tempo)
        assertEquals("3/4", firstSong.timeSignature)
        assertEquals(listOf("Test", "Persistent", "Rock"), firstSong.tags)
        assertEquals(true, firstSong.isFavorite)
        assertEquals(1234567890L, firstSong.createdAt)
        assertEquals("user_42", firstSong.createdByUserId)
        assertEquals("Tester", firstSong.createdByName)

        val secondSong = loadedSongs.first { it.id == "persistent_song_2" }
        assertEquals("Second Song", secondSong.title)
        assertEquals("Solo Artist", secondSong.artist)
    }

    @Test
    fun testSaveAndLoadSetlists_jsonRoundtrip() {
        val customSetlists = listOf(
            Setlist(
                id = "persistent_setlist_1",
                title = "Sunday Service November",
                description = "Special thanksgiving setlist",
                createdByUserId = "leader_1",
                createdByName = "Adoración",
                songs = listOf(
                    SetlistSong(
                        songId = "song_a",
                        songTitle = "Song A",
                        artist = "Artist A",
                        originalKey = "C",
                        transposedKey = "D"
                    ),
                    SetlistSong(
                        songId = "song_b",
                        songTitle = "Song B",
                        artist = "Artist B",
                        originalKey = "G",
                        transposedKey = "A"
                    )
                ),
                createdAt = 9876543210L
            )
        )

        storage.saveLocalSetlists(customSetlists)

        val reloadedStorage = LocalPersistentStorage(filesDir)
        val loadedSetlists = reloadedStorage.loadLocalSetlists()

        assertEquals(1, loadedSetlists.size)

        val setlist = loadedSetlists.first()
        assertEquals("persistent_setlist_1", setlist.id)
        assertEquals("Sunday Service November", setlist.title)
        assertEquals("Special thanksgiving setlist", setlist.description)
        assertEquals("leader_1", setlist.createdByUserId)
        assertEquals("Adoración", setlist.createdByName)
        assertEquals(9876543210L, setlist.createdAt)
        assertEquals(2, setlist.songs.size)

        val song1 = setlist.songs[0]
        assertEquals("song_a", song1.songId)
        assertEquals("Song A", song1.songTitle)
        assertEquals("D", song1.transposedKey)

        val song2 = setlist.songs[1]
        assertEquals("song_b", song2.songId)
        assertEquals("A", song2.transposedKey)
    }

    @Test
    fun testLocalSongRepositoryPersistence() = runBlocking {
        val songRepo = LocalSongRepository(persistentStorage = storage)

        val newSong = Song(
            id = "repo_song_1",
            title = "Repository Song",
            artist = "Repo Artist",
            originalKey = "E",
            content = "[E] Song content"
        )

        songRepo.addSong(newSong)

        val reloadedStorage = LocalPersistentStorage(filesDir)
        val songsAfterAdd = reloadedStorage.loadLocalSongs()
        assertTrue(songsAfterAdd.any { it.id == "repo_song_1" })

        val updatedSong = newSong.copy(title = "Updated Repository Song", isFavorite = true)
        songRepo.updateSong(updatedSong)

        val songsAfterUpdate = LocalPersistentStorage(filesDir).loadLocalSongs()
        val fetchedUpdated = songsAfterUpdate.firstOrNull { it.id == "repo_song_1" }
        assertNotNull(fetchedUpdated)
        assertEquals("Updated Repository Song", fetchedUpdated!!.title)
        assertTrue(fetchedUpdated.isFavorite)

        songRepo.deleteSong("repo_song_1")
        val songsAfterDelete = LocalPersistentStorage(filesDir).loadLocalSongs()
        assertTrue(songsAfterDelete.none { it.id == "repo_song_1" })
    }

    @Test
    fun testLocalSetlistRepositoryPersistence() = runBlocking {
        val setlistRepo = LocalSetlistRepository(persistentStorage = storage)

        val newSetlist = Setlist(
            id = "repo_setlist_1",
            title = "Concert Night",
            description = "Friday night live"
        )

        setlistRepo.createSetlist(newSetlist)

        val setlistsAfterCreate = LocalPersistentStorage(filesDir).loadLocalSetlists()
        assertTrue(setlistsAfterCreate.any { it.id == "repo_setlist_1" })

        val songToAdd = Song(
            id = "s_1",
            title = "Way Maker",
            artist = "Sinach",
            originalKey = "E",
            content = "..."
        )
        setlistRepo.addSongToSetlist("repo_setlist_1", songToAdd, "G")

        val setlistsAfterAddSong = LocalPersistentStorage(filesDir).loadLocalSetlists()
        val updatedSetlist = setlistsAfterAddSong.firstOrNull { it.id == "repo_setlist_1" }
        assertNotNull(updatedSetlist)
        assertEquals(1, updatedSetlist!!.songs.size)
        assertEquals("G", updatedSetlist.songs[0].transposedKey)

        setlistRepo.updateSetlistSongKey("repo_setlist_1", "s_1", "A")
        val setlistsAfterKeyUpdate = LocalPersistentStorage(filesDir).loadLocalSetlists()
        val keyUpdatedSetlist = setlistsAfterKeyUpdate.first { it.id == "repo_setlist_1" }
        assertEquals("A", keyUpdatedSetlist.songs[0].transposedKey)

        setlistRepo.removeSongFromSetlist("repo_setlist_1", "s_1")
        val setlistsAfterSongRemoved = LocalPersistentStorage(filesDir).loadLocalSetlists()
        val songRemovedSetlist = setlistsAfterSongRemoved.first { it.id == "repo_setlist_1" }
        assertTrue(songRemovedSetlist.songs.isEmpty())

        setlistRepo.deleteSetlist("repo_setlist_1")
        val setlistsAfterDelete = LocalPersistentStorage(filesDir).loadLocalSetlists()
        assertTrue(setlistsAfterDelete.none { it.id == "repo_setlist_1" })
    }
}
