package com.example.songchords

import com.example.songchords.model.Song
import com.example.songchords.repository.CloudSyncEngine
import com.example.songchords.repository.CloudSyncRepository
import com.example.songchords.repository.FirestoreRestCloudSyncProvider
import com.example.songchords.repository.LocalSongRepository
import com.example.songchords.repository.SyncStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CloudSyncEngineTest {

    private lateinit var localRepository: LocalSongRepository
    private lateinit var cloudProvider: FirestoreRestCloudSyncProvider
    private lateinit var syncEngine: CloudSyncEngine
    private lateinit var cloudSyncRepository: CloudSyncRepository

    @Before
    fun setUp() {
        localRepository = LocalSongRepository(initialSongs = emptyList())
        cloudProvider = FirestoreRestCloudSyncProvider(initialRemoteSongs = emptyList(), isOnline = true)
        syncEngine = CloudSyncEngine(localRepository, cloudProvider)
        cloudSyncRepository = CloudSyncRepository(localRepository, cloudProvider, syncEngine)
    }

    @Test
    fun testSyncStatusTransitions_andLastSyncedTimestamp() = runBlocking {
        assertEquals(SyncStatus.IDLE, syncEngine.syncStatus.value)
        assertNull(syncEngine.lastSyncedAt.value)

        val status = syncEngine.triggerSync()

        assertEquals(SyncStatus.SYNCED, status)
        assertEquals(SyncStatus.SYNCED, syncEngine.syncStatus.value)
        assertNotNull(syncEngine.lastSyncedAt.value)
    }

    @Test
    fun testBidirectionalSongMerging() = runBlocking {
        val localSong = Song(
            id = "local_1",
            title = "Local Hymn",
            artist = "Local Artist",
            originalKey = "G",
            content = "[G] Praise [C] Lord"
        )
        localRepository.addSong(localSong)

        val remoteSong = Song(
            id = "remote_1",
            title = "Remote Praise",
            artist = "Remote Artist",
            originalKey = "D",
            content = "[D] Sing [A] Hallelujah"
        )
        cloudProvider.saveRemoteSong(remoteSong)

        val status = syncEngine.triggerSync()
        assertEquals(SyncStatus.SYNCED, status)

        val localSongs = localRepository.getSongs().first()
        assertEquals(2, localSongs.size)
        assertTrue(localSongs.any { it.id == "remote_1" })
        assertTrue(localSongs.any { it.id == "local_1" })

        val remoteSongs = cloudProvider.fetchRemoteSongs()
        assertEquals(2, remoteSongs.size)
        assertTrue(remoteSongs.any { it.id == "remote_1" })
        assertTrue(remoteSongs.any { it.id == "local_1" })
    }

    @Test
    fun testCustomSongCloudPersistence_viaRepository() = runBlocking {
        val newCustomSong = Song(
            id = "custom_cloud_1",
            title = "Cloud Worship",
            artist = "Worship Leader",
            originalKey = "E",
            content = "[E] Holy [B] Holy"
        )

        cloudSyncRepository.addSong(newCustomSong)

        val localFetched = cloudSyncRepository.getSongById("custom_cloud_1")
        assertNotNull(localFetched)
        assertEquals("Cloud Worship", localFetched!!.title)

        val cloudFetched = cloudProvider.getRemoteSong("custom_cloud_1")
        assertNotNull(cloudFetched)
        assertEquals("Cloud Worship", cloudFetched!!.title)

        val updatedSong = newCustomSong.copy(title = "Cloud Worship Updated")
        cloudSyncRepository.updateSong(updatedSong)

        assertEquals("Cloud Worship Updated", cloudProvider.getRemoteSong("custom_cloud_1")!!.title)

        cloudSyncRepository.deleteSong("custom_cloud_1")
        assertNull(cloudSyncRepository.getSongById("custom_cloud_1"))
        assertNull(cloudProvider.getRemoteSong("custom_cloud_1"))
    }

    @Test
    fun testOfflineBehavior_transitionsToOfflineError() = runBlocking {
        cloudProvider.isOnline = false

        val status = syncEngine.triggerSync()

        assertEquals(SyncStatus.OFFLINE_ERROR, status)
        assertEquals(SyncStatus.OFFLINE_ERROR, syncEngine.syncStatus.value)
    }

    @Test
    fun testConflictResolution_prefersNewerTimestamp() = runBlocking {
        val olderTimestamp = System.currentTimeMillis() - 10000
        val newerTimestamp = System.currentTimeMillis()

        val localOlder = Song(
            id = "conflict_1",
            title = "Old Title",
            artist = "Artist",
            originalKey = "C",
            content = "Old content",
            createdAt = olderTimestamp
        )
        val remoteNewer = Song(
            id = "conflict_1",
            title = "New Title",
            artist = "Artist",
            originalKey = "C",
            content = "New content",
            createdAt = newerTimestamp
        )

        localRepository.addSong(localOlder)
        cloudProvider.saveRemoteSong(remoteNewer)

        syncEngine.triggerSync()

        val resolvedLocal = localRepository.getSongById("conflict_1")
        assertNotNull(resolvedLocal)
        assertEquals("New Title", resolvedLocal!!.title)
    }
}
