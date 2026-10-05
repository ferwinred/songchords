package com.example.songchords.ui.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.example.songchords.model.Song
import com.example.songchords.repository.CloudSyncRepository
import com.example.songchords.repository.LocalSetlistRepository
import com.example.songchords.repository.SetlistRepository
import com.example.songchords.repository.SongRepository
import com.example.songchords.ui.editor.SongEditorScreen
import com.example.songchords.ui.editor.SongEditorViewModel
import com.example.songchords.ui.setlist.AddToSetlistDialog
import com.example.songchords.ui.setlist.CreateSetlistDialog
import com.example.songchords.ui.setlist.SetlistScreen
import com.example.songchords.ui.setlist.SetlistViewModel
import com.example.songchords.ui.songlist.SongListDetailScreen
import com.example.songchords.ui.songlist.SongListViewModel
import com.example.songchords.ui.songlist.components.SongDetailPane
import com.example.songchords.ui.subscription.SubscriptionScreen
import com.example.songchords.ui.ai.AiAssistantScreen
import com.example.songchords.ui.tools.ToolsScreen
import com.example.songchords.ui.tools.ToolsTab

@Composable
fun SongChordsNavHost(
    modifier: Modifier = Modifier,
    repository: SongRepository = remember { CloudSyncRepository() },
    setlistRepository: SetlistRepository = remember { LocalSetlistRepository() }
) {
    val backStack = rememberNavBackStack(SongListRoute)
    val songListViewModel: SongListViewModel = viewModel(
        factory = SongListViewModel.Factory(repository)
    )
    val setlistViewModel: SetlistViewModel = viewModel(
        factory = SetlistViewModel.Factory(setlistRepository)
    )

    val context = LocalContext.current
    var songToAddToSetlist by remember { mutableStateOf<Song?>(null) }
    var showCreateSetlistFromDialog by remember { mutableStateOf(false) }

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
                        val isSetlistsSelected = currentKey is SetlistRoute
                        val isTunerSelected = currentKey is TunerRoute || currentKey is ToolsRoute
                        val isLibrarySelected = currentKey is ChordLibraryRoute
                        val isAiAssistantSelected = currentKey is AiAssistantRoute
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
                            selected = isSetlistsSelected,
                            onClick = {
                                if (currentKey !is SetlistRoute) {
                                    backStack.add(SetlistRoute)
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                                    contentDescription = stringResource(R.string.nav_setlists)
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
                            selected = isAiAssistantSelected,
                            onClick = {
                                if (currentKey !is AiAssistantRoute) {
                                    backStack.add(AiAssistantRoute)
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Rounded.AutoAwesome,
                                    contentDescription = stringResource(R.string.nav_ai_assistant)
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
                                    },
                                    onOpenSubscription = {
                                        backStack.add(SubscriptionRoute)
                                    },
                                    onAddToSetlist = { song ->
                                        songToAddToSetlist = song
                                    }
                                )
                            }
                        }
                        is SetlistRoute -> {
                            NavEntry(key) {
                                SetlistScreen(
                                    viewModel = setlistViewModel,
                                    onOpenSong = { songId, transposedKey ->
                                        backStack.add(
                                            SongDetailRoute(
                                                songId = songId,
                                                initialTransposedKey = transposedKey
                                            )
                                        )
                                    },
                                    onOpenSubscription = {
                                        backStack.add(SubscriptionRoute)
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
                        is SubscriptionRoute -> {
                            NavEntry(key) {
                                SubscriptionScreen(
                                    onNavigateBack = {
                                        if (backStack.size > 1) {
                                            backStack.removeAt(backStack.size - 1)
                                        }
                                    }
                                )
                            }
                        }
                        is AiAssistantRoute -> {
                            NavEntry(key) {
                                AiAssistantScreen(
                                    onOpenSubscription = {
                                        backStack.add(SubscriptionRoute)
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
                                    initialTransposedKey = key.initialTransposedKey,
                                    onToggleFavorite = songListViewModel::onToggleFavorite,
                                    onAddToSetlistClick = { songToAddToSetlist = it },
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
                                    },
                                    onOpenSubscription = {
                                        backStack.add(SubscriptionRoute)
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
                                },
                                onAddToSetlist = { song ->
                                    songToAddToSetlist = song
                                }
                            )
                        }
                    }
                }
            )
        }
    }

    songToAddToSetlist?.let { song ->
        val setlists by setlistViewModel.setlists.collectAsStateWithLifecycle()
        AddToSetlistDialog(
            song = song,
            setlists = setlists,
            onDismissRequest = { songToAddToSetlist = null },
            onConfirm = { setlistId, selectedKey ->
                setlistViewModel.addSongToSetlist(setlistId, song, selectedKey)
                val targetSetlist = setlists.firstOrNull { it.id == setlistId }
                val toastMsg = context.getString(
                    R.string.song_added_to_setlist_toast,
                    targetSetlist?.title ?: ""
                )
                Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
            },
            onCreateNewSetlist = {
                showCreateSetlistFromDialog = true
            }
        )
    }

    if (showCreateSetlistFromDialog) {
        CreateSetlistDialog(
            onDismissRequest = { showCreateSetlistFromDialog = false },
            onCreateSetlist = { title, desc ->
                setlistViewModel.createSetlist(title, desc)
            }
        )
    }
}
