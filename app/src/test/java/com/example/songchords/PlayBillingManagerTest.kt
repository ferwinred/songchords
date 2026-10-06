package com.example.songchords

import com.example.songchords.subscription.PlayBillingManager
import com.example.songchords.subscription.SubscriptionManager
import com.example.songchords.subscription.UserTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class PlayBillingManagerTest {

    @Before
    fun setUp() {
        PlayBillingManager.resetToDefault()
        SubscriptionManager.resetToDefault()
    }

    @Test
    fun testGetTierForProductId() {
        assertEquals(UserTier.BASIC_SUB, PlayBillingManager.getTierForProductId(PlayBillingManager.SUB_BASIC))
        assertEquals(UserTier.PRO_SUB, PlayBillingManager.getTierForProductId(PlayBillingManager.SUB_PRO))
        assertEquals(UserTier.PREMIUM_AI_SUB, PlayBillingManager.getTierForProductId(PlayBillingManager.SUB_PREMIUM_AI))
        assertNull(PlayBillingManager.getTierForProductId("unknown_product"))
    }

    @Test
    fun testGetProductIdForTier() {
        assertEquals(PlayBillingManager.SUB_BASIC, PlayBillingManager.getProductIdForTier(UserTier.BASIC_SUB))
        assertEquals(PlayBillingManager.SUB_PRO, PlayBillingManager.getProductIdForTier(UserTier.PRO_SUB))
        assertEquals(PlayBillingManager.SUB_PREMIUM_AI, PlayBillingManager.getProductIdForTier(UserTier.PREMIUM_AI_SUB))
        assertNull(PlayBillingManager.getProductIdForTier(UserTier.ANONYMOUS))
    }

    @Test
    fun testProcessSuccessfulPurchase() {
        PlayBillingManager.processSuccessfulPurchase(PlayBillingManager.SUB_PRO)

        assertEquals(UserTier.PRO_SUB, SubscriptionManager.currentTier.value)
        assertEquals(PlayBillingManager.SUB_PRO, PlayBillingManager.lastPurchasedProductId.value)
    }

    @Test
    fun testProcessSuccessfulPurchasePremiumAi() {
        PlayBillingManager.processSuccessfulPurchase(PlayBillingManager.SUB_PREMIUM_AI)

        assertEquals(UserTier.PREMIUM_AI_SUB, SubscriptionManager.currentTier.value)
        assertEquals(PlayBillingManager.SUB_PREMIUM_AI, PlayBillingManager.lastPurchasedProductId.value)
    }
}
