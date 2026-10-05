package com.example.songchords.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object SongListRoute : NavKey

@Serializable
data class SongDetailRoute(
    val songId: String,
    val initialTransposedKey: String? = null
) : NavKey

@Serializable
data object SetlistRoute : NavKey

@Serializable
data class SongEditorRoute(val songId: String? = null) : NavKey

@Serializable
data object ToolsRoute : NavKey

@Serializable
data object TunerRoute : NavKey

@Serializable
data object ChordLibraryRoute : NavKey

@Serializable
data object SubscriptionRoute : NavKey

@Serializable
data object AiAssistantRoute : NavKey

