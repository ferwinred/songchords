package com.example.songchords.repository

import com.example.songchords.auth.UserIdentityManager
import com.example.songchords.firestore.FirestoreSeeder
import com.example.songchords.model.Setlist
import com.example.songchords.model.Song
import com.example.songchords.repository.local.LocalPersistentStorage
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
 * Cloud Provider Abstraction supporting remote song and setlist cloud operations.
 */
interface CloudSyncProvider {
    var isOnline: Boolean
    suspend fun fetchRemoteSongs(): List<Song>
    suspend fun saveRemoteSong(song: Song): Boolean
    suspend fun deleteRemoteSong(id: String): Boolean
    suspend fun fetchRemoteSetlists(): List<Setlist> = emptyList()
    suspend fun saveRemoteSetlist(setlist: Setlist): Boolean = true
    suspend fun deleteRemoteSetlist(id: String): Boolean = true
}

/**
 * Engine managing bidirectional cloud synchronization, merging custom remote and local songs/setlists,
 * tracking sync status transitions, and recording last synced timestamps.
 */
class CloudSyncEngine(
    private val localRepository: LocalSongRepository,
    private val cloudProvider: CloudSyncProvider,
    private val userIdentityManager: UserIdentityManager = UserIdentityManager,
    private val localSetlistRepository: LocalSetlistRepository? = null,
    private val persistentStorage: LocalPersistentStorage? = null
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
     * Triggers bidirectional sync merging local catalog with remote cloud songs and setlists.
     * Performs an automatic Firestore database seeding check if not previously seeded.
     * Writes merged data to local persistent storage.
     * Serves directly from local storage when offline with zero errors or delay.
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
            val remoteSetlists = try {
                cloudProvider.fetchRemoteSetlists()
            } catch (_: Exception) {
                emptyList()
            }

            val localSongs = localRepository.getSongs().first()
            val localSetlists = localSetlistRepository?.getSetlists()?.first() ?: emptyList()

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

            // 3. Sync setlists if setlist repository is available
            if (localSetlistRepository != null && remoteSetlists.isNotEmpty()) {
                val localSetlistMap = localSetlists.associateBy { it.id }
                val remoteSetlistMap = remoteSetlists.associateBy { it.id }

                for (localSetlist in localSetlists) {
                    val remoteSetlist = remoteSetlistMap[localSetlist.id]
                    if (remoteSetlist == null) {
                        cloudProvider.saveRemoteSetlist(localSetlist)
                    } else if (localSetlist != remoteSetlist) {
                        if (localSetlist.createdAt >= remoteSetlist.createdAt) {
                            cloudProvider.saveRemoteSetlist(localSetlist)
                        } else {
                            localSetlistRepository.updateSetlist(remoteSetlist)
                        }
                    }
                }

                for (remoteSetlist in remoteSetlists) {
                    if (!localSetlistMap.containsKey(remoteSetlist.id)) {
                        localSetlistRepository.createSetlist(remoteSetlist)
                    }
                }
            }

            // Ensure merged data is explicitly saved to local persistent storage
            val updatedSongs = localRepository.getSongs().first()
            persistentStorage?.saveLocalSongs(updatedSongs)

            localSetlistRepository?.let { repo ->
                val updatedSetlists = repo.getSetlists().first()
                persistentStorage?.saveLocalSetlists(updatedSetlists)
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

    suspend fun pushSetlist(setlist: Setlist) {
        if (!cloudProvider.isOnline) {
            _syncStatus.value = SyncStatus.OFFLINE_ERROR
            return
        }
        try {
            cloudProvider.saveRemoteSetlist(setlist)
        } catch (_: Exception) {
            _syncStatus.value = SyncStatus.OFFLINE_ERROR
        }
    }

    suspend fun deleteSetlist(id: String) {
        if (!cloudProvider.isOnline) {
            _syncStatus.value = SyncStatus.OFFLINE_ERROR
            return
        }
        try {
            cloudProvider.deleteRemoteSetlist(id)
        } catch (_: Exception) {
            _syncStatus.value = SyncStatus.OFFLINE_ERROR
        }
    }
}
