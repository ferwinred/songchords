package com.example.songchords.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object SongListRoute : NavKey

@Serializable
data class SongDetailRoute(val songId: String) : NavKey

@Serializable
data class SongEditorRoute(val songId: String? = null) : NavKey

@Serializable
data object ToolsRoute : NavKey

@Serializable
data object TunerRoute : NavKey

@Serializable
data object ChordLibraryRoute : NavKey
