package com.example.songchords

import com.example.songchords.repository.LocalSongRepository
import com.example.songchords.repository.SampleSongs
import com.example.songchords.ui.editor.SongEditorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SongEditorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: LocalSongRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = LocalSongRepository(initialSongs = SampleSongs.ALL_SONGS)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitForNewSong() {
        val viewModel = SongEditorViewModel(songId = null, repository = repository)
        val state = viewModel.uiState.value

        assertTrue(state.isNewSong)
        assertNull(state.songId)
        assertEquals("", state.title)
        assertEquals("C", state.originalKey)
        assertFalse(state.isSaved)
    }

    @Test
    fun testLoadExistingSong() = runTest(testDispatcher) {
        val existingSong = SampleSongs.TU_POETA
        val viewModel = SongEditorViewModel(songId = existingSong.id, repository = repository)

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isNewSong)
        assertEquals(existingSong.id, state.songId)
        assertEquals(existingSong.title, state.title)
        assertEquals(existingSong.artist, state.artist)
        assertEquals(existingSong.originalKey, state.originalKey)
    }

    @Test
    fun testInsertChordTagAndHeader() {
        val viewModel = SongEditorViewModel(songId = null, repository = repository)

        viewModel.insertSectionHeader("Chorus")
        var state = viewModel.uiState.value
        assertTrue(state.contentTextFieldValue.text.contains("[Chorus]"))

        viewModel.insertChordTag("Gadd2")
        state = viewModel.uiState.value
        assertTrue(state.contentTextFieldValue.text.contains("[Gadd2]"))

        viewModel.insertCommentPrefix()
        state = viewModel.uiState.value
        assertTrue(state.contentTextFieldValue.text.contains("# "))
    }

    @Test
    fun testSmartAutocompleteAndAutoClosingBracket() {
        val viewModel = SongEditorViewModel(songId = null, repository = repository)
        viewModel.onKeyChange("G")

        // Type '['
        viewModel.onContentValueChange(androidx.compose.ui.text.input.TextFieldValue(
            text = "Tu poeta, tu [",
            selection = androidx.compose.ui.text.TextRange(14)
        ))

        var state = viewModel.uiState.value
        assertNotNull(state.autocompleteState)
        assertTrue(state.autocompleteState!!.isBracket)

        // Select 'G]' suggestion
        val suggestion = state.autocompleteState!!.suggestions.first { it.label == "G]" }
        viewModel.applyAutocompleteSuggestion(suggestion)

        state = viewModel.uiState.value
        assertEquals("Tu poeta, tu [G]", state.contentTextFieldValue.text)
        assertEquals(16, state.contentTextFieldValue.selection.start) // cursor past ']'

        // Type '#' for comment autocomplete
        viewModel.onContentValueChange(androidx.compose.ui.text.input.TextFieldValue(
            text = "Tu poeta, tu [G]\n# ",
            selection = androidx.compose.ui.text.TextRange(19)
        ))

        state = viewModel.uiState.value
        assertNotNull(state.autocompleteState)
        assertFalse(state.autocompleteState!!.isBracket)

        val commentSuggestion = state.autocompleteState!!.suggestions.first { it.label.contains("Tocar suave") }
        viewModel.applyAutocompleteSuggestion(commentSuggestion)

        state = viewModel.uiState.value
        assertTrue(state.contentTextFieldValue.text.contains("# Tocar suave con piano"))
    }

    @Test
    fun testSaveValidationFailureWhenEmptyTitle() = runTest(testDispatcher) {
        val viewModel = SongEditorViewModel(songId = null, repository = repository)
        viewModel.onContentValueChange(androidx.compose.ui.text.input.TextFieldValue("[G]Hello"))

        viewModel.saveSong()
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isSaved)
        assertNotNull(state.errorMessage)
        assertEquals("Please enter a song title.", state.errorMessage)
    }

    @Test
    fun testSaveNewSongSuccessfully() = runTest(testDispatcher) {
        val viewModel = SongEditorViewModel(songId = null, repository = repository)

        viewModel.onTitleChange("My Brand New Song")
        viewModel.onArtistChange("Local Artist")
        viewModel.onKeyChange("G")
        viewModel.onTempoChange("120")
        viewModel.onTagsChange("Rock, Custom")
        viewModel.onContentValueChange(androidx.compose.ui.text.input.TextFieldValue("[Intro]\n[G] [C]\n\n[Verse 1]\n[G]Awesome lyrics [C]here"))

        viewModel.saveSong()
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isSaved)

        val updatedSongs = repository.getSongs().first()
        val savedSong = updatedSongs.firstOrNull { it.title == "My Brand New Song" }
        assertNotNull(savedSong)
        assertEquals("Local Artist", savedSong?.artist)
        assertEquals("G", savedSong?.originalKey)
        assertEquals(120, savedSong?.tempo)
        assertTrue(savedSong?.tags?.contains("Rock") == true)
    }
}
