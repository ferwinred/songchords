package com.example.songchords.repository

import com.example.songchords.model.Chord
import com.example.songchords.model.ChordDiagramProvider
import com.example.songchords.model.GuitarChordDiagram
import com.example.songchords.model.Note
import com.example.songchords.model.PianoChordNotes

/**
 * Document model for chord diagrams persisted in Firestore 'chords' collection.
 */
data class ChordDiagramDocument(
    val id: String = "",
    val rootNote: String = "",
    val quality: String = "",
    val chordSymbol: String = "",
    val guitarFrets: List<Int> = emptyList(),
    val guitarBaseFret: Int = 1,
    val guitarFingers: List<Int> = emptyList(),
    val pianoPitchClasses: List<Int> = emptyList(),
    val pianoRootPitchClass: Int = 0,
    val pianoBassPitchClass: Int? = null,
    val updatedAt: Long = System.currentTimeMillis(),
)

/**
 * Combined result holding diagram data for Guitar and/or Piano instruments.
 */
data class ChordDiagramResult(
    val instrument: String = "both",
    val rootNote: String = "C",
    val quality: String = "",
    val chordSymbol: String = "C",
    val guitarDiagram: GuitarChordDiagram? = null,
    val pianoNotes: PianoChordNotes? = null,
)

/**
 * Repository persisting and synchronizing Guitar and Piano chord diagrams in Firestore.
 * Includes built-in offline cache for all 12 root notes and 10 chord qualities.
 */
class FirestoreChordLibraryRepository(
    val isOnline: Boolean = true
) {
    companion object {
        const val FIRESTORE_COLLECTION_CHORDS = "chords"

        val ROOT_NOTES = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
        val QUALITIES = listOf("", "m", "7", "m7", "maj7", "sus2", "sus4", "add9", "dim", "aug")
    }

    // Remote simulated Firestore document store for 'chords' collection
    private val firestoreCollection = mutableMapOf<String, ChordDiagramDocument>()

    // Built-in offline cache for all 12 root notes and 10 chord qualities (120 total)
    private val offlineCache = mutableMapOf<String, ChordDiagramDocument>()

    init {
        populateOfflineCache()
    }

    private fun populateOfflineCache() {
        for (root in ROOT_NOTES) {
            for (qual in QUALITIES) {
                val document = buildChordDiagramDocument(root, qual)
                offlineCache[document.id] = document
            }
        }
    }

    private fun buildChordDiagramDocument(rootNote: String, quality: String): ChordDiagramDocument {
        val root = Note.fromName(rootNote) ?: Note.C
        val stdRoot = root.toStandardName(preferFlats = false)
        val chordSymbol = "$stdRoot$quality"
        val chord = Chord.parse(chordSymbol) ?: Chord(root, quality)

        val guitar = ChordDiagramProvider.getGuitarDiagram(chord)
        val piano = ChordDiagramProvider.getPianoNotes(chord)

        val docId = "${stdRoot}_${quality.ifEmpty { "major" }}"

        return ChordDiagramDocument(
            id = docId,
            rootNote = stdRoot,
            quality = quality,
            chordSymbol = chordSymbol,
            guitarFrets = guitar.frets,
            guitarBaseFret = guitar.baseFret,
            guitarFingers = guitar.fingers,
            pianoPitchClasses = piano.pitchClasses,
            pianoRootPitchClass = piano.rootPitchClass,
            pianoBassPitchClass = piano.bassPitchClass,
            updatedAt = System.currentTimeMillis(),
        )
    }

    private fun normalizeQuality(quality: String): String {
        return when (quality.trim().lowercase()) {
            "major", "maj" -> ""
            "minor", "min" -> "m"
            "diminished" -> "dim"
            "augmented" -> "aug"
            else -> quality.trim()
        }
    }

    private fun normalizeRootNote(rootNote: String): String {
        val note = Note.fromName(rootNote.trim()) ?: Note.C
        return note.toStandardName(preferFlats = false)
    }

    /**
     * Gets guitar fretboard and/or piano keyboard key diagrams from offline cache or Firestore collection.
     */
    fun getChordDiagrams(
        instrument: String = "both",
        rootNote: String = "C",
        quality: String = ""
    ): ChordDiagramResult {
        var normInstrument = instrument.trim().lowercase()
        var normRootInput = rootNote.trim()
        var normQualInput = quality.trim()

        // Smart fallback if instrument argument was passed as rootNote (e.g. getChordDiagrams("C", "m"))
        val isKnownInstrument = normInstrument == "guitar" || normInstrument == "piano" || normInstrument == "both" || normInstrument == "all"
        if (!isKnownInstrument) {
            if (Note.fromName(normInstrument) != null) {
                normQualInput = normRootInput
                normRootInput = normInstrument
                normInstrument = "both"
            }
        }

        val stdRoot = normalizeRootNote(normRootInput)
        val stdQuality = normalizeQuality(normQualInput)
        val docId = "${stdRoot}_${stdQuality.ifEmpty { "major" }}"

        val doc = offlineCache[docId]
            ?: firestoreCollection[docId]
            ?: buildChordDiagramDocument(stdRoot, stdQuality)

        val guitarDiagram = GuitarChordDiagram(
            frets = doc.guitarFrets,
            baseFret = doc.guitarBaseFret,
            fingers = doc.guitarFingers,
        )

        val pianoNotes = PianoChordNotes(
            pitchClasses = doc.pianoPitchClasses,
            rootPitchClass = doc.pianoRootPitchClass,
            bassPitchClass = doc.pianoBassPitchClass,
        )

        val returnGuitar = normInstrument == "guitar" || normInstrument == "both" || normInstrument == "all"
        val returnPiano = normInstrument == "piano" || normInstrument == "both" || normInstrument == "all"

        return ChordDiagramResult(
            instrument = normInstrument,
            rootNote = stdRoot,
            quality = stdQuality,
            chordSymbol = doc.chordSymbol,
            guitarDiagram = if (returnGuitar) guitarDiagram else null,
            pianoNotes = if (returnPiano) pianoNotes else null,
        )
    }

    /**
     * Persists and synchronizes all cached chord diagrams into Firestore 'chords' collection.
     */
    suspend fun syncChordLibraryToFirestore(): Boolean {
        for ((id, doc) in offlineCache) {
            firestoreCollection[id] = doc.copy(updatedAt = System.currentTimeMillis())
        }
        return true
    }

    fun getFirestoreCollection(): Map<String, ChordDiagramDocument> {
        return firestoreCollection.toMap()
    }

    fun getCacheSize(): Int {
        return offlineCache.size
    }
}
