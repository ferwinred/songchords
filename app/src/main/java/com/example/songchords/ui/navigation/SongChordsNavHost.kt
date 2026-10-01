package com.example.songchords.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.GridOn
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import androidx.navigationevent.compose.rememberNavigationEventDispatcherOwner
import com.example.songchords.R
import com.example.songchords.repository.CloudSyncRepository
import com.example.songchords.repository.SongRepository
import com.example.songchords.ui.editor.SongEditorScreen
import com.example.songchords.ui.editor.SongEditorViewModel
import com.example.songchords.ui.songlist.SongListDetailScreen
import com.example.songchords.ui.songlist.SongListViewModel
import com.example.songchords.ui.songlist.components.SongDetailPane
import com.example.songchords.ui.tools.ToolsScreen
import com.example.songchords.ui.tools.ToolsTab

@Composable
fun SongChordsNavHost(
    modifier: Modifier = Modifier,
    repository: SongRepository = remember { CloudSyncRepository() }
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
        val currentKey = backStack.lastOrNull()
        val isKeyboardOpen = WindowInsets.ime.asPaddingValues().calculateBottomPadding() > 0.dp

        Scaffold(
            modifier = modifier.fillMaxSize(),
            bottomBar = {
                if (!isKeyboardOpen) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ) {
                        val isSongsSelected = currentKey is SongListRoute || currentKey is SongDetailRoute
                        val isTunerSelected = currentKey is TunerRoute || currentKey is ToolsRoute
                        val isLibrarySelected = currentKey is ChordLibraryRoute
                        val isNewSongSelected = currentKey is SongEditorRoute && currentKey.songId == null

                        NavigationBarItem(
                            selected = isSongsSelected,
                            onClick = {
                                while (backStack.size > 1) {
                                    backStack.removeAt(backStack.size - 1)
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Rounded.MusicNote,
                                    contentDescription = stringResource(R.string.nav_songs)
                                )
                            },
                            label = null,
                            alwaysShowLabel = false,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        NavigationBarItem(
                            selected = isTunerSelected,
                            onClick = {
                                if (currentKey !is TunerRoute) {
                                    backStack.add(TunerRoute)
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Rounded.Tune,
                                    contentDescription = stringResource(R.string.nav_tuner)
                                )
                            },
                            label = null,
                            alwaysShowLabel = false,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        NavigationBarItem(
                            selected = isLibrarySelected,
                            onClick = {
                                if (currentKey !is ChordLibraryRoute) {
                                    backStack.add(ChordLibraryRoute)
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Rounded.GridOn,
                                    contentDescription = stringResource(R.string.nav_chord_library)
                                )
                            },
                            label = null,
                            alwaysShowLabel = false,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        NavigationBarItem(
                            selected = isNewSongSelected,
                            onClick = {
                                if (currentKey !is SongEditorRoute || currentKey.songId != null) {
                                    backStack.add(SongEditorRoute(null))
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Rounded.Add,
                                    contentDescription = stringResource(R.string.nav_new_song)
                                )
                            },
                            label = null,
                            alwaysShowLabel = false,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavDisplay(
                backStack = backStack,
                onBack = {
                    if (backStack.size > 1) {
                        backStack.removeAt(backStack.size - 1)
                    }
                },
                modifier = Modifier.padding(innerPadding),
                entryProvider = { key ->
                    when (key) {
                        is SongListRoute -> {
                            NavEntry(key) {
                                SongListDetailScreen(
                                    viewModel = songListViewModel,
                                    onOpenEditor = { songId ->
                                        backStack.add(SongEditorRoute(songId))
                                    },
                                    onOpenTools = {
                                        backStack.add(ToolsRoute)
                                    }
                                )
                            }
                        }
                        is ToolsRoute -> {
                            NavEntry(key) {
                                ToolsScreen(
                                    onNavigateBack = {
                                        if (backStack.size > 1) {
                                            backStack.removeAt(backStack.size - 1)
                                        }
                                    },
                                    initialTab = ToolsTab.TUNER
                                )
                            }
                        }
                        is TunerRoute -> {
                            NavEntry(key) {
                                ToolsScreen(
                                    onNavigateBack = {
                                        if (backStack.size > 1) {
                                            backStack.removeAt(backStack.size - 1)
                                        }
                                    },
                                    initialTab = ToolsTab.TUNER
                                )
                            }
                        }
                        is ChordLibraryRoute -> {
                            NavEntry(key) {
                                ToolsScreen(
                                    onNavigateBack = {
                                        if (backStack.size > 1) {
                                            backStack.removeAt(backStack.size - 1)
                                        }
                                    },
                                    initialTab = ToolsTab.CHORD_LIBRARY
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
                                },
                                onOpenTools = {
                                    backStack.add(ToolsRoute)
                                }
                            )
                        }
                    }
                }
            )
        }
    }
}
