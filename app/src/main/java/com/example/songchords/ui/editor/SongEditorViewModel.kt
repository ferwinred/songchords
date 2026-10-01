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

data class AutocompleteSuggestion(
    val label: String,
    val textToInsert: String,
    val isBracket: Boolean = true
)

data class AutocompleteState(
    val isBracket: Boolean,
    val triggerIndex: Int,
    val typedPrefix: String,
    val suggestions: List<AutocompleteSuggestion>
)

data class SongEditorUiState(
    val songId: String? = null,
    val isNewSong: Boolean = true,
    val title: String = "",
    val artist: String = "",
    val originalKey: String = "C",
    val comments: String = "",
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
                comments = comments.ifBlank { null },
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

    val autocompleteState: AutocompleteState?
        get() {
            val text = contentTextFieldValue.text
            val cursorPos = contentTextFieldValue.selection.start
            if (cursorPos <= 0 || cursorPos > text.length) return null

            val lineStart = text.lastIndexOf('\n', cursorPos - 1).let { if (it == -1) 0 else it + 1 }
            val textOnLine = text.substring(lineStart, cursorPos)

            // Check for '[' trigger
            val lastBracket = textOnLine.lastIndexOf('[')
            if (lastBracket != -1) {
                val closingBracket = textOnLine.indexOf(']', lastBracket)
                if (closingBracket == -1) {
                    val globalBracketIdx = lineStart + lastBracket
                    val prefix = textOnLine.substring(lastBracket + 1)

                    val chordSuggestions = quickChordsForKey.map { chord ->
                        AutocompleteSuggestion(
                            label = "$chord]",
                            textToInsert = "$chord]",
                            isBracket = true
                        )
                    }
                    val sectionSuggestions = listOf("Intro", "Estrofa", "Coro", "Puente", "Final", "Verse 1", "Chorus", "Bridge", "Outro").map { sec ->
                        AutocompleteSuggestion(
                            label = "$sec]",
                            textToInsert = "$sec]",
                            isBracket = true
                        )
                    }

                    val allBracketSuggestions = (chordSuggestions + sectionSuggestions).filter {
                        prefix.isEmpty() || it.label.removeSuffix("]").contains(prefix, ignoreCase = true)
                    }

                    if (allBracketSuggestions.isNotEmpty()) {
                        return AutocompleteState(
                            isBracket = true,
                            triggerIndex = globalBracketIdx,
                            typedPrefix = prefix,
                            suggestions = allBracketSuggestions
                        )
                    }
                }
            }

            // Check for '#' trigger
            val lastHash = textOnLine.lastIndexOf('#')
            if (lastHash != -1) {
                val globalHashIdx = lineStart + lastHash
                val prefix = textOnLine.substring(lastHash + 1).trimStart()

                val commentTemplates = listOf(
                    "Tocar suave con piano",
                    "Nota: Entrada con batería",
                    "Nota: ",
                    "Solo de guitarra",
                    "Repetir coro",
                    "Usar Capo en traste 2"
                ).map { comment ->
                    val textToInsert = if (comment.startsWith(" ")) comment else " $comment"
                    AutocompleteSuggestion(
                        label = comment.trim(),
                        textToInsert = textToInsert,
                        isBracket = false
                    )
                }

                val filteredComments = commentTemplates.filter {
                    prefix.isEmpty() || it.label.contains(prefix, ignoreCase = true)
                }

                if (filteredComments.isNotEmpty()) {
                    return AutocompleteState(
                        isBracket = false,
                        triggerIndex = globalHashIdx,
                        typedPrefix = prefix,
                        suggestions = filteredComments
                    )
                }
            }

            return null
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
                        comments = song.comments ?: "",
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

    fun onCommentsChange(newComments: String) {
        _uiState.update { it.copy(comments = newComments) }
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

    fun insertCommentPrefix() {
        val currentTFV = _uiState.value.contentTextFieldValue
        val textToInsert = "# "
        val currentText = currentTFV.text
        val selection = currentTFV.selection

        val newText = StringBuilder(currentText)
            .insert(selection.start, textToInsert)
            .toString()

        val newCursorPos = selection.start + textToInsert.length

        _uiState.update {
            it.copy(
                contentTextFieldValue = TextFieldValue(
                    text = newText,
                    selection = TextRange(newCursorPos)
                )
            )
        }
    }

    fun insertCommentTag(commentText: String = "...") {
        val currentTFV = _uiState.value.contentTextFieldValue
        val tagToInsert = "[Comentario: $commentText]"
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

    fun applyAutocompleteSuggestion(suggestion: AutocompleteSuggestion) {
        val state = _uiState.value.autocompleteState ?: return
        val currentTFV = _uiState.value.contentTextFieldValue
        val text = currentTFV.text
        val cursorPos = currentTFV.selection.start

        val triggerIdx = state.triggerIndex
        if (triggerIdx >= text.length) return

        val replaceStart = triggerIdx + 1
        val suffixStartsWithBracket = state.isBracket && text.substring(cursorPos).startsWith("]")
        val suffixStartsWithSpace = !state.isBracket && text.substring(cursorPos).startsWith(" ")
        val replaceEnd = if (suffixStartsWithBracket || suffixStartsWithSpace) cursorPos + 1 else cursorPos

        val newText = StringBuilder(text)
            .replace(replaceStart, replaceEnd, suggestion.textToInsert)
            .toString()

        val newCursorPos = replaceStart + suggestion.textToInsert.length

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
            val existingSong = if (state.songId != null) repository.getSongById(state.songId) else null
            val currentUserId = com.example.songchords.auth.UserIdentityManager.currentUserId
            val currentUserName = com.example.songchords.auth.UserIdentityManager.currentUserName

            val songToSave = Song(
                id = existingId,
                title = state.title.trim(),
                artist = state.artist.ifBlank { "Unknown Artist" }.trim(),
                originalKey = state.originalKey,
                content = state.contentTextFieldValue.text,
                comments = state.comments.ifBlank { null },
                tempo = tempoInt,
                timeSignature = state.timeSignature.ifBlank { "4/4" },
                tags = tagList,
                isFavorite = existingSong?.isFavorite ?: false,
                createdAt = existingSong?.createdAt ?: System.currentTimeMillis(),
                createdByUserId = existingSong?.createdByUserId ?: currentUserId,
                createdByName = existingSong?.createdByName ?: currentUserName
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

    fun importFromJsonUri(context: android.content.Context, uri: android.net.Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val importedSong = com.example.songchords.utils.SongJsonUtils.importSongFromUri(context, uri)
            if (importedSong != null) {
                _uiState.update {
                    it.copy(
                        title = importedSong.title,
                        artist = importedSong.artist,
                        originalKey = importedSong.originalKey,
                        comments = importedSong.comments ?: "",
                        tempoText = importedSong.tempo?.toString() ?: "",
                        timeSignature = importedSong.timeSignature ?: "4/4",
                        tagsText = importedSong.tags.joinToString(", "),
                        contentTextFieldValue = TextFieldValue(importedSong.content),
                        errorMessage = null
                    )
                }
                onResult(true)
            } else {
                onResult(false)
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
