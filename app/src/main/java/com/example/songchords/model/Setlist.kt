package com.example.songchords.model

import com.example.songchords.auth.UserIdentityManager
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
) {
    /**
     * Checks whether the given user ID is the owner of this setlist.
     */
    fun isOwnedBy(userId: String?): Boolean {
        return (createdByUserId == null) || (createdByUserId == userId)
    }

    /**
     * Checks whether this setlist can be deleted by the given user ID or email.
     * Returns true if the user is the owner or SuperAdmin.
     */
    fun isDeletableBy(userId: String?, email: String? = null): Boolean {
        if (userId == UserIdentityManager.SUPER_ADMIN_ID || email == UserIdentityManager.SUPER_ADMIN_EMAIL) {
            return true
        }
        return isOwnedBy(userId)
    }
}
