package com.example.songchords.repository

import com.example.songchords.auth.UserIdentityManager
import com.example.songchords.firestore.FirestoreSeeder
import com.example.songchords.model.Song
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Synchronization lifecycle status for multi-device cloud persistence.
 */
enum class SyncStatus {
    IDLE,
    SYNCING,
    SYNCED,
    OFFLINE_ERROR
}

/**
 * Cloud Provider Abstraction supporting remote song cloud operations.
 */
interface CloudSyncProvider {
    var isOnline: Boolean
    suspend fun fetchRemoteSongs(): List<Song>
    suspend fun saveRemoteSong(song: Song): Boolean
    suspend fun deleteRemoteSong(id: String): Boolean
}

/**
 * Engine managing bidirectional cloud synchronization, merging custom remote and local songs,
 * tracking sync status transitions, and recording last synced timestamps.
 */
class CloudSyncEngine(
    private val localRepository: LocalSongRepository,
    private val cloudProvider: CloudSyncProvider,
    private val userIdentityManager: UserIdentityManager = UserIdentityManager,
) {
    private val _syncStatus = MutableStateFlow(SyncStatus.IDLE)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    private val _lastSyncedAt = MutableStateFlow<Long?>(null)
    val lastSyncedAt: StateFlow<Long?> = _lastSyncedAt.asStateFlow()

    init {
        if (!userIdentityManager.isFirestoreSeeded()) {
            CoroutineScope(Dispatchers.IO).launch {
                runOneTimeSeeding()
            }
        }
    }

    private suspend fun runOneTimeSeeding() {
        if (!userIdentityManager.isFirestoreSeeded()) {
            try {
                val firestore = try {
                    FirebaseFirestore.getInstance()
                } catch (_: Throwable) {
                    null
                }
                FirestoreSeeder.seedAllCollections(
                    firestore = firestore,
                    cloudProvider = cloudProvider
                )
            } catch (_: Exception) {
                // Gracefully handle uninitialized Firebase or network issues
            }
            userIdentityManager.markFirestoreSeeded()
        }
    }

    /**
     * Triggers bidirectional sync merging local catalog with remote cloud songs.
     * Performs an automatic Firestore database seeding check if not previously seeded.
     */
    suspend fun triggerSync(): SyncStatus {
        _syncStatus.value = SyncStatus.SYNCING
        try {
            if (!cloudProvider.isOnline) {
                _syncStatus.value = SyncStatus.OFFLINE_ERROR
                return SyncStatus.OFFLINE_ERROR
            }

            // Automatic Firestore seeding check on cloud sync trigger
            runOneTimeSeeding()

            val remoteSongs = cloudProvider.fetchRemoteSongs()
            val localSongs = localRepository.getSongs().first()

            val localMap = localSongs.associateBy { it.id }
            val remoteMap = remoteSongs.associateBy { it.id }

            // 1. Sync local songs up to remote
            for (localSong in localSongs) {
                val remoteSong = remoteMap[localSong.id]
                if (remoteSong == null) {
                    // Local custom or modified song not in cloud -> push to cloud
                    cloudProvider.saveRemoteSong(localSong)
                } else if (localSong != remoteSong) {
                    // Conflict resolution: prefer newer timestamp
                    if (localSong.createdAt >= remoteSong.createdAt) {
                        cloudProvider.saveRemoteSong(localSong)
                    } else {
                        localRepository.updateSong(remoteSong)
                    }
                }
            }

            // 2. Sync remote songs down to local
            for (remoteSong in remoteSongs) {
                if (!localMap.containsKey(remoteSong.id)) {
                    localRepository.addSong(remoteSong)
                }
            }

            val now = System.currentTimeMillis()
            _lastSyncedAt.value = now
            _syncStatus.value = SyncStatus.SYNCED
            return SyncStatus.SYNCED
        } catch (_: Exception) {
            _syncStatus.value = SyncStatus.OFFLINE_ERROR
            return SyncStatus.OFFLINE_ERROR
        }
    }

    suspend fun pushSong(song: Song) {
        if (!cloudProvider.isOnline) {
            _syncStatus.value = SyncStatus.OFFLINE_ERROR
            return
        }
        try {
            cloudProvider.saveRemoteSong(song)
        } catch (_: Exception) {
            _syncStatus.value = SyncStatus.OFFLINE_ERROR
        }
    }

    suspend fun deleteSong(id: String) {
        if (!cloudProvider.isOnline) {
            _syncStatus.value = SyncStatus.OFFLINE_ERROR
            return
        }
        try {
            cloudProvider.deleteRemoteSong(id)
        } catch (_: Exception) {
            _syncStatus.value = SyncStatus.OFFLINE_ERROR
        }
    }
}
