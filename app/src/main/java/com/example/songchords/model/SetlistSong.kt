package com.example.songchords.model

/**
 * Domain model representing a song within a setlist, storing custom transposed key override.
 */
data class SetlistSong(
    val songId: String,
    val songTitle: String,
    val artist: String,
    val originalKey: String,
    val transposedKey: String
)
