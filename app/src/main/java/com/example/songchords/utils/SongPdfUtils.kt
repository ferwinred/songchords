package com.example.songchords.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.songchords.R
import com.example.songchords.model.ChordLyricsLine
import com.example.songchords.model.NotationSystem
import com.example.songchords.model.ParsedSong
import com.example.songchords.model.Song
import java.io.File
import java.io.FileOutputStream

/**
 * Utility for generating, saving, and sharing clean high-resolution PDF song sheets.
 */
object SongPdfUtils {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 40f

    fun generatePdfDocument(
        context: Context,
        song: Song,
        parsedSong: ParsedSong,
        notationSystem: NotationSystem = NotationSystem.STANDARD
    ): PdfDocument {
        val pdfDocument = PdfDocument()

        val primaryColor = 0xFF1A365D.toInt()
        val accentColor = 0xFF2B6CB0.toInt()
        val chordBgColor = 0xFFEBF8FF.toInt()
        val chordTextColor = 0xFF2C5282.toInt()
        val textColor = 0xFF2D3748.toInt()
        val secondaryTextColor = 0xFF718096.toInt()
        val lineDividerColor = 0xFFE2E8F0.toInt()

        val titlePaint = Paint().apply {
            color = primaryColor
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val artistPaint = Paint().apply {
            color = secondaryTextColor
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            isAntiAlias = true
        }

        val metaLabelPaint = Paint().apply {
            color = textColor
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val metaBgPaint = Paint().apply {
            color = 0xFFEDF2F7.toInt()
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val sectionHeaderPaint = Paint().apply {
            color = accentColor
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val chordTextPaint = Paint().apply {
            color = chordTextColor
            textSize = 10f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            isAntiAlias = true
        }

        val chordBgPaint = Paint().apply {
            color = chordBgColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val lyricTextPaint = Paint().apply {
            color = textColor
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val footerPaint = Paint().apply {
            color = secondaryTextColor
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val dividerPaint = Paint().apply {
            color = lineDividerColor
            strokeWidth = 1f
            isAntiAlias = true
        }

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        var yPos = MARGIN

        fun drawFooter(pageNum: Int) {
            val footerText = "${song.title} - ${song.artist} | Page $pageNum"
            val footerWidth = footerPaint.measureText(footerText)
            canvas.drawText(footerText, (PAGE_WIDTH - footerWidth) / 2f, PAGE_HEIGHT - 20f, footerPaint)
        }

        fun newPage() {
            drawFooter(pageNumber)
            pdfDocument.finishPage(page)
            pageNumber++
            pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            yPos = MARGIN + 10f
        }

        fun drawHeader() {
            canvas.drawText(song.title, MARGIN, yPos + 20f, titlePaint)
            yPos += 26f

            canvas.drawText(song.artist, MARGIN, yPos + 12f, artistPaint)
            yPos += 24f

            val keyName = parsedSong.currentKey?.name(notationSystem) ?: song.originalKey
            val metaItems = mutableListOf<String>()
            metaItems.add("${context.getString(R.string.filter_key)}: $keyName")
            song.tempo?.let { metaItems.add("BPM: $it") }
            if (!song.timeSignature.isNullOrBlank()) {
                metaItems.add("Compás: ${song.timeSignature}")
            }
            if (song.tags.isNotEmpty()) {
                metaItems.add("Tags: ${song.tags.joinToString(", ")}")
            }

            var xMeta = MARGIN
            for (item in metaItems) {
                val itemWidth = metaLabelPaint.measureText(item) + 16f
                val rect = RectF(xMeta, yPos, xMeta + itemWidth, yPos + 18f)
                canvas.drawRoundRect(rect, 4f, 4f, metaBgPaint)
                canvas.drawText(item, xMeta + 8f, yPos + 12f, metaLabelPaint)
                xMeta += itemWidth + 8f
                if (xMeta > PAGE_WIDTH - MARGIN - 50f) break
            }
            yPos += 28f

            canvas.drawLine(MARGIN, yPos, PAGE_WIDTH - MARGIN, yPos, dividerPaint)
            yPos += 16f
        }

        drawHeader()

        val effectivePreferFlats = parsedSong.preferFlats
        for (line in parsedSong.lines) {
            when (line) {
                is ChordLyricsLine.SectionHeader -> {
                    if (yPos + 35f > PAGE_HEIGHT - MARGIN - 30f) {
                        newPage()
                    }
                    yPos += 10f
                    val headerTitle = "[${line.title.uppercase()}]"
                    canvas.drawText(headerTitle, MARGIN, yPos + 12f, sectionHeaderPaint)
                    yPos += 22f
                }
                is ChordLyricsLine.EmptyLine -> {
                    yPos += 12f
                }
                is ChordLyricsLine.ChordLyrics -> {
                    val hasChords = line.chordPositions.isNotEmpty()
                    val lineHeightNeeded = if (hasChords) 34f else 18f

                    if (yPos + lineHeightNeeded > PAGE_HEIGHT - MARGIN - 30f) {
                        newPage()
                    }

                    if (hasChords) {
                        val chordY = yPos + 10f
                        val lyricY = yPos + 28f

                        if (line.plainText.isNotEmpty()) {
                            canvas.drawText(line.plainText, MARGIN, lyricY, lyricTextPaint)
                        }

                        for (cp in line.chordPositions) {
                            val charIdx = cp.charIndex.coerceIn(0, maxOf(0, line.plainText.length))
                            val textBefore = line.plainText.substring(0, charIdx)
                            val xOffset = MARGIN + lyricTextPaint.measureText(textBefore)

                            val formattedChord = cp.chord.formatted(notationSystem, effectivePreferFlats)
                            val chordWidth = chordTextPaint.measureText(formattedChord) + 8f

                            val badgeRect = RectF(
                                xOffset - 2f,
                                chordY - 10f,
                                xOffset + chordWidth - 2f,
                                chordY + 3f
                            )
                            canvas.drawRoundRect(badgeRect, 3f, 3f, chordBgPaint)
                            canvas.drawText(formattedChord, xOffset + 2f, chordY, chordTextPaint)
                        }

                        yPos += 32f
                    } else {
                        if (line.plainText.isNotEmpty()) {
                            canvas.drawText(line.plainText, MARGIN, yPos + 12f, lyricTextPaint)
                            yPos += 18f
                        }
                    }
                }
            }
        }

        drawFooter(pageNumber)
        pdfDocument.finishPage(page)

        return pdfDocument
    }

    fun savePdfToDownloads(
        context: Context,
        song: Song,
        parsedSong: ParsedSong,
        notationSystem: NotationSystem = NotationSystem.STANDARD
    ): Uri? {
        val pdfDocument = generatePdfDocument(context, song, parsedSong, notationSystem)
        val sanitizedTitle = song.title.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val filename = "$sanitizedTitle.pdf"
        val resolver = context.contentResolver

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                uri?.also { u ->
                    resolver.openOutputStream(u)?.use { stream ->
                        pdfDocument.writeTo(stream)
                    }
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }
                val file = File(downloadsDir, filename)
                FileOutputStream(file).use { stream ->
                    pdfDocument.writeTo(stream)
                }
                Uri.fromFile(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            pdfDocument.close()
        }
    }

    fun sharePdf(
        context: Context,
        song: Song,
        parsedSong: ParsedSong,
        notationSystem: NotationSystem = NotationSystem.STANDARD
    ) {
        val pdfDocument = generatePdfDocument(context, song, parsedSong, notationSystem)
        try {
            val sanitizedTitle = song.title.replace(Regex("[^a-zA-Z0-9._-]"), "_")
            val filename = "$sanitizedTitle.pdf"
            val cacheDir = File(context.cacheDir, "shared_songs")
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            val cacheFile = File(cacheDir, filename)
            FileOutputStream(cacheFile).use { stream ->
                pdfDocument.writeTo(stream)
            }

            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                cacheFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_SUBJECT, "${song.title} - ${song.artist}")
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
        } finally {
            pdfDocument.close()
        }
    }
}
