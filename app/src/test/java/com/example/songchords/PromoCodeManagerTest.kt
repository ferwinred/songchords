package com.example.songchords

import com.example.songchords.subscription.PromoCodeManager
import com.example.songchords.subscription.SubscriptionManager
import com.example.songchords.subscription.UserTier
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PromoCodeManagerTest {

    @Before
    fun setUp() {
        PromoCodeManager.resetToDefault()
        SubscriptionManager.resetToDefault()
    }

    @Test
    fun testRedeemBetaProCode() = runBlocking {
        val userId = "test_user_001"
        val result = PromoCodeManager.redeemPromoCode("BETA-PRO-2026", userId)

        assertTrue(result.isSuccess)
        assertEquals(UserTier.PRO_SUB, result.getOrNull())
        assertEquals(UserTier.PRO_SUB, SubscriptionManager.currentTier.value)

        val promo = PromoCodeManager.getPromoCode("BETA-PRO-2026")
        assertNotNull(promo)
        assertTrue(promo!!.isRedeemed)
        assertEquals(userId, promo.redeemedByUserId)
    }

    @Test
    fun testRedeemVipPrayerCode() = runBlocking {
        val userId = "test_user_002"
        val result = PromoCodeManager.redeemPromoCode("VIP-PRAYER-99", userId)

        assertTrue(result.isSuccess)
        assertEquals(UserTier.PREMIUM_AI_SUB, result.getOrNull())
        assertEquals(UserTier.PREMIUM_AI_SUB, SubscriptionManager.currentTier.value)
    }

    @Test
    fun testRedeemBasicFreeCode() = runBlocking {
        val userId = "test_user_003"
        val result = PromoCodeManager.redeemPromoCode("BASIC-FREE-2026", userId)

        assertTrue(result.isSuccess)
        assertEquals(UserTier.BASIC_SUB, result.getOrNull())
        assertEquals(UserTier.BASIC_SUB, SubscriptionManager.currentTier.value)
    }

    @Test
    fun testSingleUseValidation() = runBlocking {
        val user1 = "user_alpha"
        val user2 = "user_beta"

        // First redemption should succeed
        val firstResult = PromoCodeManager.redeemPromoCode("BETA-PRO-2026", user1)
        assertTrue(firstResult.isSuccess)
        assertEquals(UserTier.PRO_SUB, SubscriptionManager.currentTier.value)

        // Second redemption of the same promo code should fail
        val secondResult = PromoCodeManager.redeemPromoCode("BETA-PRO-2026", user2)
        assertTrue(secondResult.isFailure)
        assertTrue(secondResult.exceptionOrNull() is IllegalStateException)

        val promo = PromoCodeManager.getPromoCode("BETA-PRO-2026")
        assertNotNull(promo)
        assertTrue(promo!!.isRedeemed)
        assertEquals(user1, promo.redeemedByUserId)
    }

    @Test
    fun testInvalidPromoCode() = runBlocking {
        val result = PromoCodeManager.redeemPromoCode("INVALID-CODE-999", "user_123")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun testEmptyPromoCode() = runBlocking {
        val result = PromoCodeManager.redeemPromoCode("   ", "user_123")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun testCaseAndWhitespaceInsensitiveRedemption() = runBlocking {
        val userId = "user_lowercase"
        val result = PromoCodeManager.redeemPromoCode("  beta-pro-2026 \n", userId)

        assertTrue(result.isSuccess)
        assertEquals(UserTier.PRO_SUB, result.getOrNull())
        assertEquals(UserTier.PRO_SUB, SubscriptionManager.currentTier.value)
    }
}
