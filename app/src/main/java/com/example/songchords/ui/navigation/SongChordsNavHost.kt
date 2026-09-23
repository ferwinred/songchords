package com.example.songchords.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import androidx.navigationevent.compose.rememberNavigationEventDispatcherOwner
import com.example.songchords.repository.LocalSongRepository
import com.example.songchords.repository.SongRepository
import com.example.songchords.ui.editor.SongEditorScreen
import com.example.songchords.ui.editor.SongEditorViewModel
import com.example.songchords.ui.songlist.SongListDetailScreen
import com.example.songchords.ui.songlist.SongListViewModel
import com.example.songchords.ui.songlist.components.SongDetailPane

@Composable
fun SongChordsNavHost(
    modifier: Modifier = Modifier,
    repository: SongRepository = remember { LocalSongRepository() }
) {
    val backStack = rememberNavBackStack(SongListRoute)
    val songListViewModel: SongListViewModel = viewModel(
        factory = SongListViewModel.Factory(repository)
    )

    val dispatcherOwner = LocalNavigationEventDispatcherOwner.current
        ?: rememberNavigationEventDispatcherOwner(parent = null)

    CompositionLocalProvider(
        LocalNavigationEventDispatcherOwner provides dispatcherOwner
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = {
                if (backStack.size > 1) {
                    backStack.removeAt(backStack.size - 1)
                }
            },
            modifier = modifier,
            entryProvider = { key ->
                when (key) {
                    is SongListRoute -> {
                        NavEntry(key) {
                            SongListDetailScreen(
                                viewModel = songListViewModel,
                                onOpenEditor = { songId ->
                                    backStack.add(SongEditorRoute(songId))
                                }
                            )
                        }
                    }
                    is SongDetailRoute -> {
                        NavEntry(key) {
                            val state by songListViewModel.uiState.collectAsStateWithLifecycle()
                            val song = state.songs.firstOrNull { it.id == key.songId }
                            SongDetailPane(
                                song = song,
                                onToggleFavorite = songListViewModel::onToggleFavorite,
                                onEditSongClick = { songId ->
                                    backStack.add(SongEditorRoute(songId))
                                },
                                onBackClick = {
                                    if (backStack.size > 1) {
                                        backStack.removeAt(backStack.size - 1)
                                    }
                                }
                            )
                        }
                    }
                    is SongEditorRoute -> {
                        NavEntry(key) {
                            val editorViewModel: SongEditorViewModel = viewModel(
                                factory = SongEditorViewModel.Factory(key.songId, repository)
                            )
                            SongEditorScreen(
                                viewModel = editorViewModel,
                                onNavigateBack = {
                                    if (backStack.size > 1) {
                                        backStack.removeAt(backStack.size - 1)
                                    }
                                }
                            )
                        }
                    }
                    else -> NavEntry(key) {
                        SongListDetailScreen(
                            viewModel = songListViewModel,
                            onOpenEditor = { songId ->
                                backStack.add(SongEditorRoute(songId))
                            }
                        )
                    }
                }
            }
        )
    }
}
