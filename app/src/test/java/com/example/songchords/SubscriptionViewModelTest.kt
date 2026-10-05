package com.example.songchords

import com.example.songchords.subscription.SubscriptionManager
import com.example.songchords.subscription.UserTier
import com.example.songchords.ui.subscription.SubscriptionViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class SubscriptionViewModelTest {

    private lateinit var viewModel: SubscriptionViewModel

    @Before
    fun setUp() {
        SubscriptionManager.resetToDefault()
        viewModel = SubscriptionViewModel()
    }

    @After
    fun tearDown() {
        SubscriptionManager.resetToDefault()
    }

    @Test
    fun testDisplayPlansExcludesAnonymous() {
        val displayPlans = viewModel.displayPlans
        assertFalse(displayPlans.contains(UserTier.ANONYMOUS))
        assertEquals(4, displayPlans.size)
        assertEquals(UserTier.REGISTERED_FREE, displayPlans[0])
        assertEquals(UserTier.BASIC_SUB, displayPlans[1])
        assertEquals(UserTier.PRO_SUB, displayPlans[2])
        assertEquals(UserTier.PREMIUM_AI_SUB, displayPlans[3])
    }

    @Test
    fun testSelectTier() = runBlocking {
        assertEquals(UserTier.REGISTERED_FREE, viewModel.currentTier.first())

        viewModel.selectTier(UserTier.PRO_SUB)
        assertEquals(UserTier.PRO_SUB, viewModel.currentTier.first())

        viewModel.selectTier(UserTier.PREMIUM_AI_SUB)
        assertEquals(UserTier.PREMIUM_AI_SUB, viewModel.currentTier.first())
    }
}
