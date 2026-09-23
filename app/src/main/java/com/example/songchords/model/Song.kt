package com.example.songchords.model

import java.util.UUID

/**
 * Domain model for a song containing chord-tagged lyrics.
 */
data class Song(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val artist: String,
    val originalKey: String,
    val content: String,
    val tempo: Int? = null,
    val timeSignature: String? = "4/4",
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
