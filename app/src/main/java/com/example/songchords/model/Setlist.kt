package com.example.songchords.model

import java.util.UUID

/**
 * Domain model representing a musical setlist containing ordered songs with custom key overrides.
 */
data class Setlist(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String? = null,
    val createdByUserId: String? = null,
    val createdByName: String? = null,
    val songs: List<SetlistSong> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
