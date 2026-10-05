package com.example.songchords.ui.subscription

import androidx.lifecycle.ViewModel
import com.example.songchords.subscription.SubscriptionManager
import com.example.songchords.subscription.UserTier
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel for SubscriptionScreen managing active subscription tier selection.
 */
class SubscriptionViewModel : ViewModel() {

    val currentTier: StateFlow<UserTier> = SubscriptionManager.currentTier

    fun selectTier(tier: UserTier) {
        SubscriptionManager.setTier(tier)
    }
}
