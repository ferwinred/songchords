package com.example.songchords

import com.example.songchords.subscription.SubscriptionManager
import com.example.songchords.subscription.UserTier
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SubscriptionManagerTest {

    @Before
    fun setUp() {
        SubscriptionManager.resetToDefault()
    }

    @After
    fun tearDown() {
        SubscriptionManager.resetToDefault()
    }

    @Test
    fun testDefaultTierIsRegisteredFree() = runBlocking {
        assertEquals(UserTier.REGISTERED_FREE, SubscriptionManager.currentTier.first())
    }

    @Test
    fun testTierProperties() {
        // ANONYMOUS
        val anon = UserTier.ANONYMOUS
        assertEquals(0, anon.maxSetlists)
        assertEquals(0, anon.maxSongsPerSetlist)
        assertEquals(0, anon.maxCustomSongs)
        assertFalse(anon.isAdFree)
        assertFalse(anon.hasAiAssistant)

        // REGISTERED_FREE
        val free = UserTier.REGISTERED_FREE
        assertEquals(3, free.maxSetlists)
        assertEquals(15, free.maxSongsPerSetlist)
        assertEquals(2, free.maxCustomSongs)
        assertFalse(free.isAdFree)
        assertFalse(free.hasAiAssistant)

        // BASIC_SUB
        val basic = UserTier.BASIC_SUB
        assertEquals(7, basic.maxSetlists)
        assertEquals(20, basic.maxSongsPerSetlist)
        assertEquals(5, basic.maxCustomSongs)
        assertTrue(basic.isAdFree)
        assertFalse(basic.hasAiAssistant)

        // PRO_SUB
        val pro = UserTier.PRO_SUB
        assertEquals(Int.MAX_VALUE, pro.maxSetlists)
        assertEquals(20, pro.maxSongsPerSetlist)
        assertEquals(30, pro.maxCustomSongs)
        assertTrue(pro.isAdFree)
        assertFalse(pro.hasAiAssistant)

        // PREMIUM_AI_SUB
        val premiumAi = UserTier.PREMIUM_AI_SUB
        assertEquals(Int.MAX_VALUE, premiumAi.maxSetlists)
        assertEquals(Int.MAX_VALUE, premiumAi.maxSongsPerSetlist)
        assertEquals(Int.MAX_VALUE, premiumAi.maxCustomSongs)
        assertTrue(premiumAi.isAdFree)
        assertTrue(premiumAi.hasAiAssistant)
    }

    @Test
    fun testCanCreateSetlistLimitEnforcement() {
        // Anonymous tier (limit 0)
        SubscriptionManager.setTier(UserTier.ANONYMOUS)
        assertFalse(SubscriptionManager.canCreateSetlist(0))

        // Registered Free tier (limit 3)
        SubscriptionManager.setTier(UserTier.REGISTERED_FREE)
        assertTrue(SubscriptionManager.canCreateSetlist(0))
        assertTrue(SubscriptionManager.canCreateSetlist(2))
        assertFalse(SubscriptionManager.canCreateSetlist(3))
        assertFalse(SubscriptionManager.canCreateSetlist(4))

        // Basic tier (limit 7)
        SubscriptionManager.setTier(UserTier.BASIC_SUB)
        assertTrue(SubscriptionManager.canCreateSetlist(6))
        assertFalse(SubscriptionManager.canCreateSetlist(7))

        // Pro tier (limit Int.MAX_VALUE)
        SubscriptionManager.setTier(UserTier.PRO_SUB)
        assertTrue(SubscriptionManager.canCreateSetlist(100))

        // Premium AI tier (limit Int.MAX_VALUE)
        SubscriptionManager.setTier(UserTier.PREMIUM_AI_SUB)
        assertTrue(SubscriptionManager.canCreateSetlist(1000))
    }

    @Test
    fun testCanAddSongToSetlistLimitEnforcement() {
        // Anonymous tier (limit 0)
        SubscriptionManager.setTier(UserTier.ANONYMOUS)
        assertFalse(SubscriptionManager.canAddSongToSetlist(0))

        // Registered Free tier (limit 15)
        SubscriptionManager.setTier(UserTier.REGISTERED_FREE)
        assertTrue(SubscriptionManager.canAddSongToSetlist(14))
        assertFalse(SubscriptionManager.canAddSongToSetlist(15))

        // Basic tier (limit 20)
        SubscriptionManager.setTier(UserTier.BASIC_SUB)
        assertTrue(SubscriptionManager.canAddSongToSetlist(19))
        assertFalse(SubscriptionManager.canAddSongToSetlist(20))

        // Pro tier (limit 20)
        SubscriptionManager.setTier(UserTier.PRO_SUB)
        assertTrue(SubscriptionManager.canAddSongToSetlist(19))
        assertFalse(SubscriptionManager.canAddSongToSetlist(20))

        // Premium AI tier (limit Int.MAX_VALUE)
        SubscriptionManager.setTier(UserTier.PREMIUM_AI_SUB)
        assertTrue(SubscriptionManager.canAddSongToSetlist(100))
    }

    @Test
    fun testCanCreateCustomSongLimitEnforcement() {
        // Anonymous tier (limit 0)
        SubscriptionManager.setTier(UserTier.ANONYMOUS)
        assertFalse(SubscriptionManager.canCreateCustomSong(0))

        // Registered Free tier (limit 2)
        SubscriptionManager.setTier(UserTier.REGISTERED_FREE)
        assertTrue(SubscriptionManager.canCreateCustomSong(0))
        assertTrue(SubscriptionManager.canCreateCustomSong(1))
        assertFalse(SubscriptionManager.canCreateCustomSong(2))

        // Basic tier (limit 5)
        SubscriptionManager.setTier(UserTier.BASIC_SUB)
        assertTrue(SubscriptionManager.canCreateCustomSong(4))
        assertFalse(SubscriptionManager.canCreateCustomSong(5))

        // Pro tier (limit 30)
        SubscriptionManager.setTier(UserTier.PRO_SUB)
        assertTrue(SubscriptionManager.canCreateCustomSong(29))
        assertFalse(SubscriptionManager.canCreateCustomSong(30))

        // Premium AI tier (limit Int.MAX_VALUE)
        SubscriptionManager.setTier(UserTier.PREMIUM_AI_SUB)
        assertTrue(SubscriptionManager.canCreateCustomSong(100))
    }

    @Test
    fun testHasAdFreeAccess() {
        SubscriptionManager.setTier(UserTier.ANONYMOUS)
        assertFalse(SubscriptionManager.hasAdFreeAccess())

        SubscriptionManager.setTier(UserTier.REGISTERED_FREE)
        assertFalse(SubscriptionManager.hasAdFreeAccess())

        SubscriptionManager.setTier(UserTier.BASIC_SUB)
        assertTrue(SubscriptionManager.hasAdFreeAccess())

        SubscriptionManager.setTier(UserTier.PRO_SUB)
        assertTrue(SubscriptionManager.hasAdFreeAccess())

        SubscriptionManager.setTier(UserTier.PREMIUM_AI_SUB)
        assertTrue(SubscriptionManager.hasAdFreeAccess())
    }

    @Test
    fun testHasAiAssistantAccess() {
        SubscriptionManager.setTier(UserTier.ANONYMOUS)
        assertFalse(SubscriptionManager.hasAiAssistantAccess())

        SubscriptionManager.setTier(UserTier.REGISTERED_FREE)
        assertFalse(SubscriptionManager.hasAiAssistantAccess())

        SubscriptionManager.setTier(UserTier.BASIC_SUB)
        assertFalse(SubscriptionManager.hasAiAssistantAccess())

        SubscriptionManager.setTier(UserTier.PRO_SUB)
        assertFalse(SubscriptionManager.hasAiAssistantAccess())

        SubscriptionManager.setTier(UserTier.PREMIUM_AI_SUB)
        assertTrue(SubscriptionManager.hasAiAssistantAccess())
    }
}
