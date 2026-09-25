package com.example.songchords

import com.example.songchords.auth.UserIdentityManager
import com.example.songchords.model.Song
import com.example.songchords.utils.SongJsonUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class SongOwnershipTest {

    @Before
    fun setup() {
        UserIdentityManager.resetToDefault()
    }

    @Test
    fun testIsOwnedBy_whenCreatedByUserIdIsNull_returnsFalseForAnyUser() {
        val publicSong = Song(
            id = "public-1",
            title = "Public Sample Song",
            artist = "Artist",
            originalKey = "C",
            content = "[C]Lyrics",
            createdByUserId = null
        )

        assertFalse(publicSong.isOwnedBy("user-1"))
        assertFalse(publicSong.isOwnedBy("user-2"))
        assertFalse(publicSong.isOwnedBy(null))
    }

    @Test
    fun testIsOwnedBy_whenCreatedByUserIdMatchesCurrentUserId_returnsTrue() {
        val ownerId = "user-abc-123"
        val song = Song(
            id = "song-1",
            title = "User Song",
            artist = "Artist",
            originalKey = "G",
            content = "[G]Lyrics",
            createdByUserId = ownerId,
            createdByName = "Test Owner"
        )

        assertTrue(song.isOwnedBy(ownerId))
    }

    @Test
    fun testIsOwnedBy_whenCreatedByUserIdDoesNotMatchCurrentUserId_returnsFalse() {
        val ownerId = "user-abc-123"
        val otherUserId = "user-xyz-789"
        val song = Song(
            id = "song-1",
            title = "User Song",
            artist = "Artist",
            originalKey = "G",
            content = "[G]Lyrics",
            createdByUserId = ownerId,
            createdByName = "Test Owner"
        )

        assertFalse(song.isOwnedBy(otherUserId))
    }

    @Test
    fun testCloneForUser_createsPersonalCopyAndUpdatesOwnership() {
        val authorId = "author-1"
        val authorName = "Author Name"
        val originalSong = Song(
            id = "orig-song-id",
            title = "Awesome Worship Song",
            artist = "Original Artist",
            originalKey = "D",
            content = "[D]Awesome lyrics [G]and chords",
            createdByUserId = authorId,
            createdByName = authorName
        )

        val newUserId = "user-cloner-2"
        val newUserName = "Cloner User"

        val clonedSong = originalSong.cloneForUser(newUserId, newUserName)

        assertNotEquals(originalSong.id, clonedSong.id)
        assertEquals(originalSong.title, clonedSong.title)
        assertEquals(originalSong.artist, clonedSong.artist)
        assertEquals(originalSong.originalKey, clonedSong.originalKey)
        assertEquals(originalSong.content, clonedSong.content)
        assertEquals(newUserId, clonedSong.createdByUserId)
        assertEquals(newUserName, clonedSong.createdByName)

        assertFalse(originalSong.isOwnedBy(newUserId))
        assertTrue(clonedSong.isOwnedBy(newUserId))
    }

    @Test
    fun testUserIdentityManager_getAndSetIdentity() {
        UserIdentityManager.setUserIdentity("custom-uid-99", "John Doe")

        assertEquals("custom-uid-99", UserIdentityManager.currentUserId)
        assertEquals("John Doe", UserIdentityManager.currentUserName)

        UserIdentityManager.resetToDefault()

        assertNotEquals("custom-uid-99", UserIdentityManager.currentUserId)
        assertTrue(UserIdentityManager.currentUserId.isNotBlank())
        assertTrue(UserIdentityManager.currentUserName.startsWith("User "))
    }

    @Test
    fun testSongJsonUtils_preservesOwnershipFieldsOnExportAndImport() {
        val songWithOwnership = Song(
            id = UUID.randomUUID().toString(),
            title = "Custom Created Song",
            artist = "Local Artist",
            originalKey = "A",
            content = "[A]Glory to God",
            createdByUserId = "uid-101",
            createdByName = "Maria"
        )

        val json = SongJsonUtils.exportToJson(songWithOwnership)
        val importedSong = SongJsonUtils.importFromJson(json)

        assertNotNull(importedSong)
        assertEquals(songWithOwnership.id, importedSong.id)
        assertEquals(songWithOwnership.createdByUserId, importedSong.createdByUserId)
        assertEquals(songWithOwnership.createdByName, importedSong.createdByName)
    }
}
