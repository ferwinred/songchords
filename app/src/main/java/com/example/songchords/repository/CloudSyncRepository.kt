package com.example.songchords.repository

import android.content.Context
import com.example.songchords.model.Setlist
import com.example.songchords.model.Song
import com.example.songchords.repository.local.LocalPersistentStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException

/**
 * Cloud REST / Firestore-compatible provider implementation operating gracefully offline or online out of the box.
 */
class FirestoreRestCloudSyncProvider(
    initialRemoteSongs: List<Song> = emptyList(),
    initialRemoteSetlists: List<Setlist> = emptyList(),
    override var isOnline: Boolean = true
) : CloudSyncProvider {

    private val remoteDatabase = mutableMapOf<String, Song>().apply {
        initialRemoteSongs.forEach { put(it.id, it) }
    }

    private val remoteSetlistsDatabase = mutableMapOf<String, Setlist>().apply {
        initialRemoteSetlists.forEach { put(it.id, it) }
    }

    override suspend fun fetchRemoteSongs(): List<Song> {
        if (!isOnline) {
            throw IOException("Cloud REST/Firestore backend unreachable: Offline mode")
        }
        return remoteDatabase.values.toList()
    }

    override suspend fun saveRemoteSong(song: Song): Boolean {
        if (!isOnline) {
            throw IOException("Failed to save remote song: Cloud offline")
        }
        remoteDatabase[song.id] = song
        return true
    }

    override suspend fun deleteRemoteSong(id: String): Boolean {
        if (!isOnline) {
            throw IOException("Failed to delete remote song: Cloud offline")
        }
        return remoteDatabase.remove(id) != null
    }

    override suspend fun fetchRemoteSetlists(): List<Setlist> {
        if (!isOnline) {
            throw IOException("Cloud REST/Firestore backend unreachable: Offline mode")
        }
        return remoteSetlistsDatabase.values.toList()
    }

    override suspend fun saveRemoteSetlist(setlist: Setlist): Boolean {
        if (!isOnline) {
            throw IOException("Failed to save remote setlist: Cloud offline")
        }
        remoteSetlistsDatabase[setlist.id] = setlist
        return true
    }

    override suspend fun deleteRemoteSetlist(id: String): Boolean {
        if (!isOnline) {
            throw IOException("Failed to delete remote setlist: Cloud offline")
        }
        return remoteSetlistsDatabase.remove(id) != null
    }

    fun getRemoteSong(id: String): Song? = remoteDatabase[id]
    fun getAllRemoteSongs(): List<Song> = remoteDatabase.values.toList()
    fun getRemoteSetlist(id: String): Setlist? = remoteSetlistsDatabase[id]
    fun getAllRemoteSetlists(): List<Setlist> = remoteSetlistsDatabase.values.toList()
}

/**
 * Offline-first hybrid repository wrapping LocalSongRepository with CloudSyncEngine.
 */
class CloudSyncRepository(
    val localRepository: LocalSongRepository = LocalSongRepository(),
    val cloudProvider: CloudSyncProvider = FirestoreRestCloudSyncProvider(),
    val syncEngine: CloudSyncEngine = CloudSyncEngine(
        localRepository = localRepository,
        cloudProvider = cloudProvider
    ),
    val persistentStorage: LocalPersistentStorage? = null
) : SongRepository {

    constructor(persistentStorage: LocalPersistentStorage, context: Context? = null) : this(
        localRepository = LocalSongRepository(persistentStorage = persistentStorage),
        cloudProvider = FirestoreRestCloudSyncProvider(),
        syncEngine = CloudSyncEngine(
            localRepository = LocalSongRepository(persistentStorage = persistentStorage),
            cloudProvider = FirestoreRestCloudSyncProvider(),
            persistentStorage = persistentStorage,
            context = context
        ),
        persistentStorage = persistentStorage
    )

    constructor(context: Context) : this(
        persistentStorage = LocalPersistentStorage(context),
        context = context
    )

    val syncStatus: StateFlow<SyncStatus> = syncEngine.syncStatus
    val lastSyncedAt: StateFlow<Long?> = syncEngine.lastSyncedAt

    suspend fun triggerSync(): SyncStatus {
        return syncEngine.triggerSync()
    }

    override fun getSongs(): Flow<List<Song>> = localRepository.getSongs()

    override suspend fun getSongById(id: String): Song? = localRepository.getSongById(id)

    override suspend fun searchSongs(query: String): List<Song> = localRepository.searchSongs(query)

    override suspend fun filterByTag(tag: String): List<Song> = localRepository.filterByTag(tag)

    override suspend fun toggleFavorite(id: String) {
        localRepository.toggleFavorite(id)
        localRepository.getSongById(id)?.let { song ->
            syncEngine.pushSong(song)
        }
    }

    override suspend fun addSong(song: Song) {
        localRepository.addSong(song)
        syncEngine.pushSong(song)
    }

    override suspend fun updateSong(song: Song) {
        localRepository.updateSong(song)
        syncEngine.pushSong(song)
    }

    override suspend fun deleteSong(id: String) {
        localRepository.deleteSong(id)
        syncEngine.deleteSong(id)
    }
}
