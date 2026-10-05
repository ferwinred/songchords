package com.example.songchords.subscription

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages active user subscription tier and checks tier limits.
 */
object SubscriptionManager {

    private val _currentTier = MutableStateFlow(UserTier.REGISTERED_FREE)
    val currentTier: StateFlow<UserTier> = _currentTier.asStateFlow()

    fun setTier(tier: UserTier) {
        _currentTier.value = tier
    }

    fun canCreateSetlist(currentCount: Int = 0, tier: UserTier = _currentTier.value): Boolean {
        return currentCount < tier.maxSetlists
    }

    fun canAddSongToSetlist(currentCount: Int = 0, tier: UserTier = _currentTier.value): Boolean {
        return currentCount < tier.maxSongsPerSetlist
    }

    fun canCreateCustomSong(currentCount: Int = 0, tier: UserTier = _currentTier.value): Boolean {
        return currentCount < tier.maxCustomSongs
    }

    fun hasAdFreeAccess(tier: UserTier = _currentTier.value): Boolean {
        return tier.isAdFree
    }

    fun hasAiAssistantAccess(tier: UserTier = _currentTier.value): Boolean {
        return tier.hasAiAssistant
    }

    fun resetToDefault() {
        _currentTier.value = UserTier.REGISTERED_FREE
    }
}
