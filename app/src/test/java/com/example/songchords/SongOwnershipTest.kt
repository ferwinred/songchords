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
    fun testUserIdentityManager_firestoreSeedingState() {
        assertFalse(UserIdentityManager.isFirestoreSeeded())

        UserIdentityManager.markFirestoreSeeded()
        assertTrue(UserIdentityManager.isFirestoreSeeded())

        UserIdentityManager.resetToDefault()
        assertFalse(UserIdentityManager.isFirestoreSeeded())
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

    @Test
    fun testSongIsEditableBy_superAdminOrOwnerReturnsTrue() {
        val song = Song(
            id = "song-owner-1",
            title = "Sample",
            artist = "Artist",
            originalKey = "C",
            content = "Lyrics",
            createdByUserId = "regular_user_1"
        )

        // Regular non-owner user -> false
        assertFalse(song.isEditableBy("other_user_2"))

        // Owner -> true
        assertTrue(song.isEditableBy("regular_user_1"))

        // SuperAdmin by ID -> true
        assertTrue(song.isEditableBy(UserIdentityManager.SUPER_ADMIN_ID))

        // SuperAdmin by email -> true
        assertTrue(song.isEditableBy("other_user_2", UserIdentityManager.SUPER_ADMIN_EMAIL))
    }

    @Test
    fun testSetlistIsDeletableBy_superAdminOrOwnerReturnsTrue() {
        val setlist = com.example.songchords.model.Setlist(
            id = "setlist-1",
            title = "My Setlist",
            createdByUserId = "user_100"
        )

        // Non-owner -> false
        assertFalse(setlist.isDeletableBy("user_200"))

        // Owner -> true
        assertTrue(setlist.isDeletableBy("user_100"))

        // SuperAdmin by ID -> true
        assertTrue(setlist.isDeletableBy(UserIdentityManager.SUPER_ADMIN_ID))

        // SuperAdmin by Email -> true
        assertTrue(setlist.isDeletableBy("user_200", UserIdentityManager.SUPER_ADMIN_EMAIL))
    }
}
