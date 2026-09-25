package com.example.songchords.ui.chordlibrary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.songchords.R
import com.example.songchords.model.Chord
import com.example.songchords.model.ChordDiagramProvider
import com.example.songchords.model.Note
import com.example.songchords.ui.theme.SongChordsTheme
import com.example.songchords.ui.viewer.components.GuitarChordDiagramView
import com.example.songchords.ui.viewer.components.PianoChordDiagramView

enum class DiagramInstrumentTab {
    GUITAR,
    PIANO
}

data class ChordQualityOption(
    val labelRes: Int,
    val suffix: String
)

@OptIn(ExperimentalLayoutApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ChordLibraryScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(DiagramInstrumentTab.GUITAR) }

    val rootNotes = remember {
        listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    }
    var selectedRootIndex by remember { mutableIntStateOf(0) }

    val qualityOptions = remember {
        listOf(
            ChordQualityOption(R.string.quality_major, ""),
            ChordQualityOption(R.string.quality_minor, "m"),
            ChordQualityOption(R.string.quality_7, "7"),
            ChordQualityOption(R.string.quality_m7, "m7"),
            ChordQualityOption(R.string.quality_maj7, "maj7"),
            ChordQualityOption(R.string.quality_sus2, "sus2"),
            ChordQualityOption(R.string.quality_sus4, "sus4"),
            ChordQualityOption(R.string.quality_add9, "add9"),
            ChordQualityOption(R.string.quality_dim, "dim"),
            ChordQualityOption(R.string.quality_aug, "aug")
        )
    }
    var selectedQualityIndex by remember { mutableIntStateOf(0) }

    val currentRootName = rootNotes[selectedRootIndex]
    val currentQuality = qualityOptions[selectedQualityIndex]
    val currentChordSymbol = "$currentRootName${currentQuality.suffix}"

    val chord = remember(currentChordSymbol) {
        Chord.parse(currentChordSymbol) ?: Chord(Note.fromName(currentRootName) ?: Note.C, currentQuality.suffix)
    }

    val pianoNotes = remember(chord) { ChordDiagramProvider.getPianoNotes(chord) }
    val guitarDiagram = remember(chord) { ChordDiagramProvider.getGuitarDiagram(chord) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.chord_library_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Instrument Tab Selector: Guitar vs Piano
        PrimaryTabRow(
            selectedTabIndex = selectedTab.ordinal,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == DiagramInstrumentTab.GUITAR,
                onClick = { selectedTab = DiagramInstrumentTab.GUITAR },
                text = {
                    Text(
                        text = "🎸 Guitarra",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
            Tab(
                selected = selectedTab == DiagramInstrumentTab.PIANO,
                onClick = { selectedTab = DiagramInstrumentTab.PIANO },
                text = {
                    Text(
                        text = "🎹 Piano",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Root Note Selector Chips
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = stringResource(R.string.root_note_label),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rootNotes.forEachIndexed { index, rootName ->
                        val isSelected = index == selectedRootIndex
                        ElevatedFilterChip(
                            selected = isSelected,
                            onClick = { selectedRootIndex = index },
                            label = { Text(rootName, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.elevatedFilterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quality Selector Chips
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = stringResource(R.string.chord_quality_label),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    qualityOptions.forEachIndexed { index, option ->
                        val isSelected = index == selectedQualityIndex
                        ElevatedFilterChip(
                            selected = isSelected,
                            onClick = { selectedQualityIndex = index },
                            label = {
                                Text(
                                    text = stringResource(option.labelRes),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.elevatedFilterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Interactive Visual Diagram Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (selectedTab) {
                    DiagramInstrumentTab.GUITAR -> {
                        GuitarChordDiagramView(
                            diagram = guitarDiagram,
                            chordName = currentChordSymbol,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    DiagramInstrumentTab.PIANO -> {
                        PianoChordDiagramView(
                            pianoNotes = pianoNotes,
                            chordName = currentChordSymbol,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Chord Notes Summary
                val activeNoteNames = pianoNotes.pitchClasses.map { Note.fromPitchClass(it).toStandardName() }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Notas: ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = activeNoteNames.joinToString(" - "),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChordLibraryScreenPreview() {
    SongChordsTheme {
        ChordLibraryScreen()
    }
}
