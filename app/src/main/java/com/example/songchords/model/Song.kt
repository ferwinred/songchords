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
    val createdAt: Long = System.currentTimeMillis(),
    val createdByUserId: String? = null,
    val createdByName: String? = null,
) {
    /**
     * Checks whether the given user ID is the owner of this song.
     * Songs with null `createdByUserId` are public/preset songs and not owned by an individual user.
     */
    fun isOwnedBy(userId: String?): Boolean {
        return (createdByUserId != null) && (createdByUserId == userId)
    }

    /**
     * Clones this song into a new editable personal copy for the specified user.
     */
    fun cloneForUser(userId: String, userName: String): Song {
        return this.copy(
            id = UUID.randomUUID().toString(),
            createdByUserId = userId,
            createdByName = userName,
            createdAt = System.currentTimeMillis()
        )
    }
}
