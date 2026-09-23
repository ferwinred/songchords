package com.example.songchords.ui.songlist.components

import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material.icons.rounded.LinearScale
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import com.example.songchords.R
import com.example.songchords.engine.ChordParser
import com.example.songchords.engine.SectionTitleLocalizer
import com.example.songchords.model.Chord
import com.example.songchords.model.ChordLyricsLine
import com.example.songchords.model.ChordPosition
import com.example.songchords.model.NotationSystem
import com.example.songchords.model.Song
import com.example.songchords.repository.SampleSongs
import com.example.songchords.ui.theme.SongChordsTheme
import com.example.songchords.ui.viewer.components.ChordDiagramDialog
import com.example.songchords.ui.viewer.components.SongViewerControls
import com.example.songchords.utils.SongExportUtils
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

data class SongSection(
    val title: String?,
    val lines: List<ChordLyricsLine>
)

private fun groupLinesIntoSections(lines: List<ChordLyricsLine>): List<SongSection> {
    val sections = mutableListOf<SongSection>()
    var currentTitle: String? = null
    var currentLines = mutableListOf<ChordLyricsLine>()

    for (line in lines) {
        if (line is ChordLyricsLine.SectionHeader) {
            if (currentLines.isNotEmpty() || currentTitle != null) {
                sections.add(SongSection(currentTitle, currentLines))
                currentLines = mutableListOf()
            }
            currentTitle = line.title
        } else {
            currentLines.add(line)
        }
    }

    if (currentLines.isNotEmpty() || currentTitle != null) {
        sections.add(SongSection(currentTitle, currentLines))
    }

    return sections
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SongDetailPane(
    song: Song?,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    onEditSongClick: ((String) -> Unit)? = null,
    showHeader: Boolean = true
) {
    if (song == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.no_song_selected),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.select_song_prompt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    val context = LocalContext.current

    var transpositionSemitones by remember(song) { mutableIntStateOf(0) }
    var notationSystem by remember { mutableStateOf(NotationSystem.STANDARD) }

    var isAutoScrolling by remember { mutableStateOf(false) }
    var scrollSpeed by remember { mutableFloatStateOf(1f) }

    var isFitToScreen by remember { mutableStateOf(false) }
    var fontScale by remember { mutableFloatStateOf(1.0f) }

    var isControlsExpanded by remember { mutableStateOf(false) }

    var selectedChordForDiagram by remember { mutableStateOf<Chord?>(null) }
    var showExportMenu by remember { mutableStateOf(false) }

    val baseParsedSong = remember(song) {
        ChordParser.parseSong(song, notationSystem)
    }

    val currentParsedSong = remember(baseParsedSong, transpositionSemitones, notationSystem) {
        baseParsedSong
            .withNotationSystem(notationSystem)
            .transpose(transpositionSemitones)
    }

    val songSections = remember(currentParsedSong) {
        groupLinesIntoSections(currentParsedSong.lines)
    }

    val lineCount = currentParsedSong.lines.size
    val fitScale = when {
        lineCount > 35 -> 0.65f
        lineCount > 20 -> 0.70f
        lineCount > 10 -> 0.75f
        else -> 0.80f
    }
    val effectiveFontScale = if (isFitToScreen) fitScale else fontScale

    val scrollState = rememberScrollState()

    // Smooth Auto-scroll Coroutine with Graceful Touch Gesture Handling
    LaunchedEffect(isAutoScrolling, scrollSpeed) {
        if (isAutoScrolling) {
            try {
                while (isActive && scrollState.value < scrollState.maxValue) {
                    val delayMillis = (30 / scrollSpeed).toLong().coerceAtLeast(10)
                    scrollState.scrollBy(1.5f)
                    delay(delayMillis)
                }
                if (scrollState.value >= scrollState.maxValue) {
                    isAutoScrolling = false
                }
            } catch (e: CancellationException) {
                if (coroutineContext.isActive) {
                    isAutoScrolling = false
                }
                throw e
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(isAutoScrolling) {
                if (!isAutoScrolling) return@pointerInput
                awaitPointerEventScope {
                    while (isAutoScrolling) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        if (event.type == PointerEventType.Move) {
                            if (event.changes.any { it.pressed }) {
                                isAutoScrolling = false
                            }
                        }
                    }
                }
            }
            .verticalScroll(scrollState)
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp)
    ) {
        if (showHeader) {
            // Top Header Row with Title, Artist, Actions & Language Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBackClick != null) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back_to_list)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (onEditSongClick != null) {
                    IconButton(onClick = { onEditSongClick(song.id) }) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = stringResource(R.string.edit_song),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(onClick = { onToggleFavorite(song.id) }) {
                    Icon(
                        imageVector = if (song.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = stringResource(R.string.favorite),
                        tint = if (song.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Export & Share Dropdown Menu
                Box {
                    IconButton(onClick = { showExportMenu = true }) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = stringResource(R.string.export_options_desc),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    DropdownMenu(
                        expanded = showExportMenu,
                        onDismissRequest = { showExportMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.download_song)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Download,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                showExportMenu = false
                                val formattedText = SongExportUtils.generateFormattedText(
                                    song = song,
                                    parsedSong = currentParsedSong,
                                    notationSystem = notationSystem
                                )
                                val savedUri = SongExportUtils.saveToDownloads(
                                    context = context,
                                    songTitle = song.title,
                                    content = formattedText
                                )
                                val message = if (savedUri != null) {
                                    context.getString(R.string.download_success)
                                } else {
                                    context.getString(R.string.download_error)
                                }
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        )

                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.share_song)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Share,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                showExportMenu = false
                                val formattedText = SongExportUtils.generateFormattedText(
                                    song = song,
                                    parsedSong = currentParsedSong,
                                    notationSystem = notationSystem
                                )
                                SongExportUtils.shareSongSheet(
                                    context = context,
                                    songTitle = song.title,
                                    artist = song.artist,
                                    content = formattedText
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Metadata Chips Row
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.original_key_format, song.originalKey),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            song.tempo?.let { tempo ->
                AssistChip(
                    onClick = {},
                    label = { Text("$tempo BPM", style = MaterialTheme.typography.labelMedium) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Speed,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                )
            }

            song.timeSignature?.let { timeSig ->
                AssistChip(
                    onClick = {},
                    label = { Text(timeSig, style = MaterialTheme.typography.labelMedium) }
                )
            }

            song.tags.forEach { tag ->
                AssistChip(
                    onClick = {},
                    label = { Text(tag, style = MaterialTheme.typography.labelMedium) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                    )
                )
            }

            // Options / Performance Options Chip
            FilterChip(
                selected = isControlsExpanded,
                onClick = { isControlsExpanded = !isControlsExpanded },
                label = {
                    Text(
                        text = stringResource(R.string.performance_options),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Tune,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive Performance Controls Panel
        AnimatedVisibility(
            visible = isControlsExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                SongViewerControls(
                    currentKeyName = currentParsedSong.currentKey?.name(notationSystem) ?: song.originalKey,
                    originalKeyName = song.originalKey,
                    transpositionSemitones = transpositionSemitones,
                    onTranspose = { delta -> transpositionSemitones += delta },
                    onResetTransposition = { transpositionSemitones = 0 },
                    notationSystem = notationSystem,
                    onNotationChange = { notationSystem = it },
                    isAutoScrolling = isAutoScrolling,
                    onToggleAutoScroll = { isAutoScrolling = !isAutoScrolling },
                    scrollSpeed = scrollSpeed,
                    onSpeedChange = { scrollSpeed = it },
                    isFitToScreen = isFitToScreen,
                    onFitToScreenToggle = { isFitToScreen = !isFitToScreen },
                    fontScale = fontScale,
                    onFontScaleChange = { fontScale = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Render Song Sections as Structured Material 3 Section Cards
        val cardInnerPadding = if (isFitToScreen) 6.dp else (16 * effectiveFontScale).dp.coerceAtLeast(8.dp)
        val cardBottomMargin = if (isFitToScreen) 8.dp else (16 * effectiveFontScale).dp.coerceAtLeast(8.dp)
        val emptyLineSpacing = if (isFitToScreen) 4.dp else (10 * effectiveFontScale).dp.coerceAtLeast(4.dp)

        songSections.forEach { section ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = cardBottomMargin),
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                border = null,
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(cardInnerPadding)
                ) {
                    section.title?.let { title ->
                        SectionTitleBadge(
                            title = title,
                            effectiveFontScale = effectiveFontScale,
                            isFitToScreen = isFitToScreen
                        )
                    }

                    section.lines.forEach { line ->
                        when (line) {
                            is ChordLyricsLine.ChordLyrics -> {
                                if (isChordOnlyLine(line)) {
                                    RenderChordOnlyLine(
                                        chordPositions = line.chordPositions,
                                        notationSystem = notationSystem,
                                        preferFlats = currentParsedSong.preferFlats,
                                        effectiveFontScale = effectiveFontScale,
                                        isFitToScreen = isFitToScreen,
                                        onChordClick = { chord -> selectedChordForDiagram = chord }
                                    )
                                } else {
                                    RenderChordLyricsLine(
                                        line = line,
                                        notationSystem = notationSystem,
                                        preferFlats = currentParsedSong.preferFlats,
                                        effectiveFontScale = effectiveFontScale,
                                        isFitToScreen = isFitToScreen,
                                        onChordClick = { chord -> selectedChordForDiagram = chord }
                                    )
                                }
                            }
                            is ChordLyricsLine.EmptyLine -> {
                                Spacer(modifier = Modifier.height(emptyLineSpacing))
                            }
                            is ChordLyricsLine.SectionHeader -> {
                                // Handled at section level
                            }
                        }
                    }
                }
            }
        }
    }

    // Chord Diagram Popup Dialog
    selectedChordForDiagram?.let { chord ->
        ChordDiagramDialog(
            chord = chord,
            notationSystem = notationSystem,
            onDismissRequest = { selectedChordForDiagram = null }
        )
    }
}

@Composable
private fun SectionTitleBadge(
    title: String,
    effectiveFontScale: Float = 1.0f,
    isFitToScreen: Boolean = false
) {
    val configuration = LocalConfiguration.current
    val isSpanish = remember(configuration) {
        val locales = AppCompatDelegate.getApplicationLocales()
        val tag = if (!locales.isEmpty) {
            locales[0]?.language ?: Locale.getDefault().language
        } else {
            Locale.getDefault().language
        }
        tag.startsWith("es", ignoreCase = true)
    }

    val localizedTitle = remember(title, isSpanish) {
        SectionTitleLocalizer.localize(title, isSpanish)
    }

    val titleLower = localizedTitle.lowercase()

    val icon = when {
        titleLower.contains("chorus") || titleLower.contains("coro") || titleLower.contains("refrain") -> {
            Icons.Rounded.MusicNote
        }
        titleLower.contains("intro") || titleLower.contains("introducci") -> {
            Icons.Rounded.PlayArrow
        }
        titleLower.contains("bridge") || titleLower.contains("puente") || titleLower.contains("solo") || titleLower.contains("outro") || titleLower.contains("final") -> {
            Icons.Rounded.LinearScale
        }
        else -> {
            Icons.Rounded.FormatQuote
        }
    }

    val displayTitle = if (localizedTitle.startsWith("[")) {
        localizedTitle.uppercase()
    } else {
        "[${localizedTitle.uppercase()}]"
    }

    val titleFontSize = (15 * effectiveFontScale).sp
    val iconSize = (18 * effectiveFontScale).dp.coerceAtLeast(14.dp)
    val bottomPadding = if (isFitToScreen) 4.dp else (8 * effectiveFontScale).dp.coerceAtLeast(4.dp)

    Row(
        modifier = Modifier.padding(bottom = bottomPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(iconSize)
        )
        Spacer(modifier = Modifier.width((6 * effectiveFontScale).dp.coerceAtLeast(4.dp)))
        Text(
            text = displayTitle,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = titleFontSize
            ),
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

private fun isChordOnlyLine(line: ChordLyricsLine.ChordLyrics): Boolean {
    if (line.chordPositions.isEmpty()) return false
    val lyricsText = line.plainText.trim()
    if (lyricsText.isEmpty()) return true
    return lyricsText.all {
        it.isWhitespace() || it == '-' || it == '|' || it == '/' || it == '\\' || it == '.' || it == ':' || it == ','
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RenderChordOnlyLine(
    chordPositions: List<ChordPosition>,
    notationSystem: NotationSystem,
    preferFlats: Boolean,
    effectiveFontScale: Float,
    isFitToScreen: Boolean,
    onChordClick: (Chord) -> Unit
) {
    val verticalPadding = if (isFitToScreen) 2.dp else (6 * effectiveFontScale).dp.coerceAtLeast(2.dp)
    val chordFontSize = (13 * effectiveFontScale).sp
    val horizontalPadding = (12 * effectiveFontScale).dp.coerceAtLeast(6.dp)
    val badgeVerticalPadding = (6 * effectiveFontScale).dp.coerceAtLeast(2.dp)
    val itemSpacing = (8 * effectiveFontScale).dp.coerceAtLeast(4.dp)

    FlowRow(
        modifier = Modifier.padding(vertical = verticalPadding),
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        verticalArrangement = Arrangement.spacedBy(itemSpacing)
    ) {
        chordPositions.forEach { cp ->
            val chord = cp.chord
            Surface(
                shape = RoundedCornerShape((8 * effectiveFontScale).dp.coerceAtLeast(4.dp)),
                color = MaterialTheme.colorScheme.primaryContainer,
                tonalElevation = 3.dp,
                shadowElevation = 2.dp,
                onClick = { onChordClick(chord) }
            ) {
                Text(
                    text = chord.formatted(notationSystem, preferFlats),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = chordFontSize,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = horizontalPadding, vertical = badgeVerticalPadding)
                )
            }
        }
    }
}

@Composable
private fun RenderChordLyricsLine(
    line: ChordLyricsLine.ChordLyrics,
    notationSystem: NotationSystem,
    preferFlats: Boolean,
    effectiveFontScale: Float,
    isFitToScreen: Boolean,
    onChordClick: (Chord) -> Unit
) {
    val textMeasurer = rememberTextMeasurer()
    val lyricFontSize = (16 * effectiveFontScale).sp
    val lyricStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = lyricFontSize,
        fontWeight = FontWeight.Medium
    )

    val textLayoutResult = remember(line.plainText, lyricStyle) {
        textMeasurer.measure(
            text = line.plainText,
            style = lyricStyle
        )
    }

    val verticalPadding = if (isFitToScreen) 2.dp else (4 * effectiveFontScale).dp.coerceAtLeast(2.dp)
    val chordBoxHeight = (28 * effectiveFontScale).dp.coerceAtLeast(18.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = verticalPadding)
    ) {
        // Row 1 (Chord Line)
        if (line.chordPositions.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chordBoxHeight)
            ) {
                line.chordPositions.forEach { cp ->
                    val xPx = textLayoutResult.getHorizontalPosition(
                        cp.charIndex.coerceIn(0, maxOf(0, line.plainText.length)),
                        usePrimaryDirection = true
                    )
                    var badgeWidthPx by remember { mutableFloatStateOf(0f) }
                    val centeredXPx = maxOf(0f, xPx - (badgeWidthPx / 2f))

                    ChordBadge(
                        chord = cp.chord,
                        notationSystem = notationSystem,
                        preferFlats = preferFlats,
                        effectiveFontScale = effectiveFontScale,
                        onClick = { onChordClick(cp.chord) },
                        modifier = Modifier
                            .onSizeChanged { size -> badgeWidthPx = size.width.toFloat() }
                            .offset { IntOffset(centeredXPx.roundToInt(), 0) }
                    )
                }
            }
        }

        // Row 2 (Lyric Line)
        if (line.plainText.isNotEmpty()) {
            Text(
                text = line.plainText,
                style = lyricStyle,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ChordBadge(
    chord: Chord,
    notationSystem: NotationSystem,
    preferFlats: Boolean,
    effectiveFontScale: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chordFontSize = (11 * effectiveFontScale).sp
    val horizontalPadding = (8 * effectiveFontScale).dp.coerceAtLeast(4.dp)
    val verticalPadding = (3 * effectiveFontScale).dp.coerceAtLeast(1.dp)

    Surface(
        shape = RoundedCornerShape((8 * effectiveFontScale).dp.coerceAtLeast(4.dp)),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp,
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = chord.formatted(notationSystem, preferFlats),
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = chordFontSize,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = verticalPadding)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SongDetailPanePreview() {
    SongChordsTheme {
        SongDetailPane(
            song = SampleSongs.TU_POETA,
            onToggleFavorite = {}
        )
    }
}
