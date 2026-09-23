package com.example.songchords

import com.example.songchords.repository.LocalSongRepository
import com.example.songchords.repository.SampleSongs
import com.example.songchords.ui.songlist.SongListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SongListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: LocalSongRepository
    private lateinit var viewModel: SongListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = LocalSongRepository(SampleSongs.ALL_SONGS)
        viewModel = SongListViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialUiState_loadsAllSongs() = runTest {
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertEquals(SampleSongs.ALL_SONGS.size, state.songs.size)
        assertFalse(state.isLoading)
        assertTrue(state.availableKeys.contains("G"))
        assertTrue(state.availableKeys.contains("D"))
        assertTrue(state.availableArtists.contains("Alex Campos"))
    }

    @Test
    fun searchQuery_filtersByTitleAndArtistAndKey() = runTest {
        advanceUntilIdle()

        // Search by artist
        viewModel.onSearchQueryChange("Marcos Witt")
        advanceUntilIdle()
        var state = viewModel.uiState.value
        assertEquals(1, state.songs.size)
        assertEquals("Renuévame", state.songs.first().title)

        // Search by key/chord
        viewModel.onSearchQueryChange("D")
        advanceUntilIdle()
        state = viewModel.uiState.value
        assertTrue(state.songs.any { it.title == "Renuévame" })

        // Search by title
        viewModel.onSearchQueryChange("Tu Poeta")
        advanceUntilIdle()
        state = viewModel.uiState.value
        assertEquals(1, state.songs.size)
        assertEquals("Alex Campos", state.songs.first().artist)
    }

    @Test
    fun keyFilter_filtersSongsByExactKey() = runTest {
        advanceUntilIdle()

        viewModel.onKeyFilterSelect("A")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.songs.size)
        assertEquals("Cuan Grande Es Él", state.songs.first().title)
    }

    @Test
    fun artistFilter_filtersSongsByArtist() = runTest {
        advanceUntilIdle()

        viewModel.onArtistFilterSelect("Marcos Witt")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.songs.size)
        assertEquals("Renuévame", state.songs.first().title)
    }

    @Test
    fun favoriteFilter_and_toggleFavorite() = runTest {
        advanceUntilIdle()

        // Initially no favorites
        viewModel.onToggleFavoritesFilter()
        advanceUntilIdle()
        var state = viewModel.uiState.value
        assertEquals(0, state.songs.size)

        // Toggle favorite for Tu Poeta
        viewModel.onToggleFavorite("sample_tu_poeta")
        advanceUntilIdle()

        state = viewModel.uiState.value
        assertEquals(1, state.songs.size)
        assertEquals("Tu Poeta", state.songs.first().title)
    }

    @Test
    fun clearFilters_resetsState() = runTest {
        advanceUntilIdle()

        viewModel.onSearchQueryChange("Witt")
        viewModel.onKeyFilterSelect("D")
        viewModel.onArtistFilterSelect("Marcos Witt")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isAnyFilterActive)

        viewModel.onClearFilters()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isAnyFilterActive)
        assertEquals("", state.searchQuery)
        assertNull(state.selectedArtistFilter)
        assertNull(state.selectedKeyFilter)
        assertEquals(SampleSongs.ALL_SONGS.size, state.songs.size)
    }
}
