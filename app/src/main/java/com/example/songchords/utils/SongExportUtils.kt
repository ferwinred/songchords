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
import com.example.songchords.model.NotationSystem
import com.example.songchords.model.ParsedSong
import com.example.songchords.model.Song
import java.io.File

object SongExportUtils {

    fun generateFormattedText(
        song: Song,
        parsedSong: ParsedSong,
        notationSystem: NotationSystem
    ): String {
        val keyName = parsedSong.currentKey?.name(notationSystem) ?: song.originalKey
        val sb = StringBuilder()
        sb.appendLine(song.title)
        sb.appendLine("Artist: ${song.artist}")
        sb.appendLine("Key: $keyName")
        if (song.tempo != null) {
            sb.appendLine("Tempo: ${song.tempo} BPM")
        }
        if (!song.timeSignature.isNullOrBlank()) {
            sb.appendLine("Time Signature: ${song.timeSignature}")
        }
        if (song.tags.isNotEmpty()) {
            sb.appendLine("Tags: ${song.tags.joinToString(", ")}")
        }
        sb.appendLine()
        sb.append(parsedSong.toChordTaggedText())
        return sb.toString()
    }

    fun saveToDownloads(context: Context, songTitle: String, content: String): Uri? {
        val sanitizedTitle = songTitle.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val filename = "$sanitizedTitle.txt"
        val resolver = context.contentResolver

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                uri?.also { u ->
                    resolver.openOutputStream(u)?.use { stream ->
                        stream.write(content.toByteArray(Charsets.UTF_8))
                    }
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }
                val file = File(downloadsDir, filename)
                file.writeText(content, Charsets.UTF_8)
                Uri.fromFile(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareSongSheet(
        context: Context,
        songTitle: String,
        artist: String,
        content: String
    ) {
        try {
            val sanitizedTitle = songTitle.replace(Regex("[^a-zA-Z0-9._-]"), "_")
            val filename = "$sanitizedTitle.txt"
            val cacheDir = File(context.cacheDir, "shared_songs")
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            val cacheFile = File(cacheDir, filename)
            cacheFile.writeText(content, Charsets.UTF_8)

            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                cacheFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "$songTitle - $artist")
                putExtra(Intent.EXTRA_TEXT, content)
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
        } catch (_: Exception) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "$songTitle - $artist")
                putExtra(Intent.EXTRA_TEXT, content)
            }
            val chooserIntent = Intent.createChooser(
                shareIntent,
                context.getString(R.string.share_song)
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooserIntent)
        }
    }
}
