package com.example.songchords.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.songchords.R
import com.example.songchords.model.Song
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

/**
 * Utility for exporting, importing, saving, and sharing song backup data in JSON format.
 */
object SongJsonUtils {

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    private val songAdapter by lazy {
        moshi.adapter(Song::class.java).indent("  ")
    }

    /**
     * Serializes a [Song] object into a formatted JSON string.
     */
    fun exportToJson(song: Song): String {
        return songAdapter.toJson(song)
    }

    /**
     * Parses a JSON string into a [Song] object.
     * Uses Moshi adapter with fallback parsing for extra resilience.
     */
    fun importFromJson(jsonString: String): Song {
        val trimmed = jsonString.trim()
        require(trimmed.isNotEmpty()) { "JSON string cannot be empty" }

        val parsed = try {
            songAdapter.fromJson(trimmed)
        } catch (_: Exception) {
            parseFallbackJson(trimmed)
        } ?: parseFallbackJson(trimmed)

        return parsed.copy(
            id = parsed.id.ifBlank { java.util.UUID.randomUUID().toString() },
            title = parsed.title.ifBlank { "Imported Song" },
            artist = parsed.artist.ifBlank { "Unknown Artist" },
            originalKey = parsed.originalKey.ifBlank { "C" },
            content = parsed.content,
            timeSignature = parsed.timeSignature ?: "4/4",
            createdByUserId = parsed.createdByUserId,
            createdByName = parsed.createdByName
        )
    }

    /**
     * Fallback manual JSON parsing using regex for environments where reflective JSON adapters
     * might encounter non-standard keys or partial JSON.
     */
    private fun parseFallbackJson(json: String): Song {
        fun extractString(key: String): String? {
            val pattern = Regex("\"$key\"\\s*:\\s*\"(.*?)\"", RegexOption.DOT_MATCHES_ALL)
            return pattern.find(json)?.groupValues?.get(1)?.replace("\\\"", "\"")?.replace("\\n", "\n")
        }

        fun extractInt(key: String): Int? {
            val pattern = Regex("\"$key\"\\s*:\\s*([0-9]+)")
            return pattern.find(json)?.groupValues?.get(1)?.toIntOrNull()
        }

        fun extractBoolean(key: String): Boolean {
            val pattern = Regex("\"$key\"\\s*:\\s*(true|false)", RegexOption.IGNORE_CASE)
            return pattern.find(json)?.groupValues?.get(1)?.lowercase() == "true"
        }

        fun extractLong(key: String): Long? {
            val pattern = Regex("\"$key\"\\s*:\\s*([0-9]+)")
            return pattern.find(json)?.groupValues?.get(1)?.toLongOrNull()
        }

        fun extractTags(): List<String> {
            val arrayPattern = Regex("\"tags\"\\s*:\\s*\\[(.*)]", RegexOption.DOT_MATCHES_ALL)
            val match = arrayPattern.find(json)?.groupValues?.get(1) ?: return emptyList()
            val tagPattern = Regex("\"(.*?)\"")
            return tagPattern.findAll(match).map { it.groupValues[1] }.toList()
        }

        val id = extractString("id") ?: java.util.UUID.randomUUID().toString()
        val title = extractString("title") ?: "Imported Song"
        val artist = extractString("artist") ?: "Unknown Artist"
        val originalKey = extractString("originalKey") ?: "C"
        val content = extractString("content") ?: ""
        val tempo = extractInt("tempo")
        val timeSignature = extractString("timeSignature") ?: "4/4"
        val tags = extractTags()
        val isFavorite = extractBoolean("isFavorite")
        val createdAt = extractLong("createdAt") ?: System.currentTimeMillis()
        val createdByUserId = extractString("createdByUserId")
        val createdByName = extractString("createdByName")

        return Song(
            id = id,
            title = title,
            artist = artist,
            originalKey = originalKey,
            content = content,
            tempo = tempo,
            timeSignature = timeSignature,
            tags = tags,
            isFavorite = isFavorite,
            createdAt = createdAt,
            createdByUserId = createdByUserId,
            createdByName = createdByName
        )
    }

    /**
     * Saves a JSON backup file for a song to the device's Downloads directory.
     */
    fun saveJsonToDownloads(context: Context, song: Song): Uri? {
        val jsonContent = exportToJson(song)
        val sanitizedTitle = song.title.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val filename = "$sanitizedTitle.json"
        val resolver = context.contentResolver

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                uri?.also { u ->
                    resolver.openOutputStream(u)?.use { stream ->
                        stream.write(jsonContent.toByteArray(Charsets.UTF_8))
                    }
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }
                val file = File(downloadsDir, filename)
                file.writeText(jsonContent, Charsets.UTF_8)
                Uri.fromFile(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Shares a JSON backup file via [Intent.ACTION_SEND].
     */
    fun shareJson(context: Context, song: Song) {
        val jsonContent = exportToJson(song)
        try {
            val sanitizedTitle = song.title.replace(Regex("[^a-zA-Z0-9._-]"), "_")
            val filename = "$sanitizedTitle.json"
            val cacheDir = File(context.cacheDir, "shared_songs")
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            val cacheFile = File(cacheDir, filename)
            cacheFile.writeText(jsonContent, Charsets.UTF_8)

            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                cacheFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_SUBJECT, "${song.title} - ${song.artist}")
                putExtra(Intent.EXTRA_TEXT, jsonContent)
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooserIntent = Intent.createChooser(
                shareIntent,
                context.getString(R.string.share_song)
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooserIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Reads a JSON file from a device [Uri] and parses it into a [Song] object.
     */
    fun importSongFromUri(context: Context, uri: Uri): Song? {
        return try {
            val jsonString = context.contentResolver.openInputStream(uri)?.use { stream ->
                stream.bufferedReader(Charsets.UTF_8).readText()
            } ?: return null
            importFromJson(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
