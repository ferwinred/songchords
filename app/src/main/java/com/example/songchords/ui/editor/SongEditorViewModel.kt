package com.example.songchords.ui.editor

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.songchords.engine.ChordParser
import com.example.songchords.model.ParsedSong
import com.example.songchords.model.Song
import com.example.songchords.repository.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class SongEditorUiState(
    val songId: String? = null,
    val isNewSong: Boolean = true,
    val title: String = "",
    val artist: String = "",
    val originalKey: String = "C",
    val tempoText: String = "",
    val timeSignature: String = "4/4",
    val tagsText: String = "",
    val contentTextFieldValue: TextFieldValue = TextFieldValue(""),
    val selectedTab: Int = 0, // 0 = Edit, 1 = Preview
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
) {
    val parsedSongPreview: ParsedSong
        get() {
            val tempoInt = tempoText.toIntOrNull()
            val tagList = tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            val dummySong = Song(
                id = songId ?: "preview_id",
                title = title.ifEmpty { "Untitled Song" },
                artist = artist.ifEmpty { "Unknown Artist" },
                originalKey = originalKey,
                content = contentTextFieldValue.text,
                tempo = tempoInt,
                timeSignature = timeSignature,
                tags = tagList
            )
            return ChordParser.parseSong(dummySong)
        }

    val quickChordsForKey: List<String>
        get() {
            return when (originalKey) {
                "G" -> listOf("G", "Am", "Bm", "C", "D", "Em", "D7", "G7", "Cadd2", "D/F#", "G/B")
                "C" -> listOf("C", "Dm", "Em", "F", "G", "Am", "G7", "C7", "Cadd2", "F/A")
                "D" -> listOf("D", "Em", "F#m", "G", "A", "Bm", "A7", "D7", "D/F#", "G/B")
                "A" -> listOf("A", "Bm", "C#m", "D", "E", "F#m", "E7", "A7", "D/F#")
                "E" -> listOf("E", "F#m", "G#m", "A", "B", "C#m", "B7", "E7")
                "F" -> listOf("F", "Gm", "Am", "Bb", "C", "Dm", "C7", "F7", "Bb/D")
                "Bm" -> listOf("Bm", "C#dim", "D", "Em", "F#m", "G", "A", "F#7")
                "Em" -> listOf("Em", "F#dim", "G", "Am", "Bm", "C", "D", "B7")
                "Am" -> listOf("Am", "Bdim", "C", "Dm", "Em", "F", "G", "E7")
                else -> listOf("C", "Dm", "Em", "F", "G", "Am", "G7", "Bb", "D", "E")
            }
        }
}

class SongEditorViewModel(
    private val songId: String?,
    private val repository: SongRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SongEditorUiState(songId = songId, isNewSong = songId == null))
    val uiState: StateFlow<SongEditorUiState> = _uiState.asStateFlow()

    init {
        if (songId != null) {
            loadSong(songId)
        }
    }

    private fun loadSong(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val song = repository.getSongById(id)
            if (song != null) {
                _uiState.update {
                    it.copy(
                        songId = song.id,
                        isNewSong = false,
                        title = song.title,
                        artist = song.artist,
                        originalKey = song.originalKey,
                        tempoText = song.tempo?.toString() ?: "",
                        timeSignature = song.timeSignature ?: "4/4",
                        tagsText = song.tags.joinToString(", "),
                        contentTextFieldValue = TextFieldValue(song.content),
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Song not found.") }
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle, errorMessage = null) }
    }

    fun onArtistChange(newArtist: String) {
        _uiState.update { it.copy(artist = newArtist, errorMessage = null) }
    }

    fun onKeyChange(newKey: String) {
        _uiState.update { it.copy(originalKey = newKey) }
    }

    fun onTempoChange(newTempo: String) {
        _uiState.update { it.copy(tempoText = newTempo) }
    }

    fun onTimeSignatureChange(newTimeSig: String) {
        _uiState.update { it.copy(timeSignature = newTimeSig) }
    }

    fun onTagsChange(newTags: String) {
        _uiState.update { it.copy(tagsText = newTags) }
    }

    fun onContentValueChange(newValue: TextFieldValue) {
        _uiState.update { it.copy(contentTextFieldValue = newValue, errorMessage = null) }
    }

    fun onSelectTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    fun insertChordTag(chordName: String) {
        val currentTFV = _uiState.value.contentTextFieldValue
        val tagToInsert = "[$chordName]"
        val currentText = currentTFV.text
        val selection = currentTFV.selection

        val newText = StringBuilder(currentText)
            .insert(selection.start, tagToInsert)
            .toString()

        val newCursorPos = selection.start + tagToInsert.length

        _uiState.update {
            it.copy(
                contentTextFieldValue = TextFieldValue(
                    text = newText,
                    selection = TextRange(newCursorPos)
                )
            )
        }
    }

    fun insertSectionHeader(sectionName: String) {
        val currentTFV = _uiState.value.contentTextFieldValue
        val headerToInsert = "\n[$sectionName]\n"
        val currentText = currentTFV.text
        val selection = currentTFV.selection

        val newText = StringBuilder(currentText)
            .insert(selection.start, headerToInsert)
            .toString()

        val newCursorPos = selection.start + headerToInsert.length

        _uiState.update {
            it.copy(
                contentTextFieldValue = TextFieldValue(
                    text = newText,
                    selection = TextRange(newCursorPos)
                )
            )
        }
    }

    fun saveSong() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter a song title.") }
            return
        }
        if (state.contentTextFieldValue.text.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter song lyrics with chords.") }
            return
        }

        val tempoInt = state.tempoText.trim().toIntOrNull()
        val tagList = state.tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val existingId = state.songId ?: UUID.randomUUID().toString()
            val songToSave = Song(
                id = existingId,
                title = state.title.trim(),
                artist = state.artist.ifBlank { "Unknown Artist" }.trim(),
                originalKey = state.originalKey,
                content = state.contentTextFieldValue.text,
                tempo = tempoInt,
                timeSignature = state.timeSignature.ifBlank { "4/4" },
                tags = tagList
            )

            if (state.isNewSong) {
                repository.addSong(songToSave)
            } else {
                repository.updateSong(songToSave)
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isSaved = true
                )
            }
        }
    }

    class Factory(
        private val songId: String?,
        private val repository: SongRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SongEditorViewModel(songId, repository) as T
        }
    }
}
