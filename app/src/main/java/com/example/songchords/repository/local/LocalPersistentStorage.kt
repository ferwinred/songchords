package com.example.songchords.repository.local

import android.content.Context
import com.example.songchords.model.Setlist
import com.example.songchords.model.Song
import com.example.songchords.repository.LocalSetlistRepository
import com.example.songchords.repository.SampleSongs
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

/**
 * Manages persistent local storage for songs and setlists in internal file storage (`songs_db.json` and `setlists_db.json`).
 */
class LocalPersistentStorage(private val filesDir: File) {

    constructor(context: Context) : this(context.filesDir)

    private val songsFile: File = File(filesDir, SONGS_FILE_NAME)
    private val setlistsFile: File = File(filesDir, SETLISTS_FILE_NAME)

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    private val songsListAdapter: JsonAdapter<List<Song>> by lazy {
        val type = Types.newParameterizedType(List::class.java, Song::class.java)
        moshi.adapter(type)
    }

    private val setlistsListAdapter: JsonAdapter<List<Setlist>> by lazy {
        val type = Types.newParameterizedType(List::class.java, Setlist::class.java)
        moshi.adapter(type)
    }

    /**
     * Reads songs from local file storage (`songs_db.json`).
     * Populates [SampleSongs] on initial run or empty/missing file.
     */
    fun loadLocalSongs(): List<Song> {
        return try {
            if ((!songsFile.exists()) || (songsFile.length() == 0L)) {
                val initialSongs = SampleSongs.ALL_SONGS
                saveLocalSongs(initialSongs)
                initialSongs
            } else {
                val json = songsFile.readText(Charsets.UTF_8)
                val songs = songsListAdapter.fromJson(json)
                if (songs.isNullOrEmpty()) {
                    val initialSongs = SampleSongs.ALL_SONGS
                    saveLocalSongs(initialSongs)
                    initialSongs
                } else {
                    songs
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val initialSongs = SampleSongs.ALL_SONGS
            saveLocalSongs(initialSongs)
            initialSongs
        }
    }

    /**
     * Writes song list to internal local file storage (`songs_db.json`).
     */
    fun saveLocalSongs(songs: List<Song>) {
        try {
            if (!filesDir.exists()) {
                filesDir.mkdirs()
            }
            val json = songsListAdapter.toJson(songs)
            songsFile.writeText(json, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Reads setlists from local file storage (`setlists_db.json`).
     * Populates default setlists on initial run or empty/missing file.
     */
    fun loadLocalSetlists(): List<Setlist> {
        return try {
            if ((!setlistsFile.exists()) || (setlistsFile.length() == 0L)) {
                val initialSetlists = LocalSetlistRepository.createSampleSetlists()
                saveLocalSetlists(initialSetlists)
                initialSetlists
            } else {
                val json = setlistsFile.readText(Charsets.UTF_8)
                val setlists = setlistsListAdapter.fromJson(json)
                if (setlists.isNullOrEmpty()) {
                    val initialSetlists = LocalSetlistRepository.createSampleSetlists()
                    saveLocalSetlists(initialSetlists)
                    initialSetlists
                } else {
                    setlists
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val initialSetlists = LocalSetlistRepository.createSampleSetlists()
            saveLocalSetlists(initialSetlists)
            initialSetlists
        }
    }

    /**
     * Writes setlist list to internal local file storage (`setlists_db.json`).
     */
    fun saveLocalSetlists(setlists: List<Setlist>) {
        try {
            if (!filesDir.exists()) {
                filesDir.mkdirs()
            }
            val json = setlistsListAdapter.toJson(setlists)
            setlistsFile.writeText(json, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val SONGS_FILE_NAME = "songs_db.json"
        const val SETLISTS_FILE_NAME = "setlists_db.json"
    }
}
