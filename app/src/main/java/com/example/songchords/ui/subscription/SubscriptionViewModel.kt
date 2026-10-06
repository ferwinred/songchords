package com.example.songchords.ui.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.songchords.auth.UserIdentityManager
import com.example.songchords.subscription.PromoCodeManager
import com.example.songchords.subscription.SubscriptionManager
import com.example.songchords.subscription.UserTier
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for SubscriptionScreen managing active subscription tier selection and promo code redemption.
 */
class SubscriptionViewModel : ViewModel() {

    val currentTier: StateFlow<UserTier> = SubscriptionManager.currentTier

    val displayPlans: List<UserTier> = UserTier.entries.filter { it != UserTier.ANONYMOUS }

    fun selectTier(tier: UserTier) {
        SubscriptionManager.setTier(tier)
    }

    fun redeemPromoCode(
        code: String,
        userId: String = UserIdentityManager.currentUserId,
        onResult: (Result<UserTier>) -> Unit,
    ) {
        viewModelScope.launch {
            val result = PromoCodeManager.redeemPromoCode(code, userId)
            onResult(result)
        }
    }
}
