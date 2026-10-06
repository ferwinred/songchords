package com.example.songchords

import com.example.songchords.auth.FirebaseAuthManager
import com.example.songchords.auth.FirebaseUserState
import com.example.songchords.subscription.SubscriptionManager
import com.example.songchords.subscription.UserTier
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FirebaseAuthManagerTest {

    @Before
    fun setUp() {
        FirebaseAuthManager.resetToDefault()
        SubscriptionManager.resetToDefault()
    }

    @Test
    fun testSignInAnonymously() = runBlocking {
        val result = FirebaseAuthManager.signInAnonymously()

        assertTrue(result.isSuccess)
        val state = result.getOrNull()
        assertTrue(state != null)
        assertTrue(state!!.isAnonymous)
        assertTrue(state.isSignedIn)
        assertTrue(FirebaseAuthManager.isAnonymous)
    }

    @Test
    fun testSignInWithGoogle() = runBlocking {
        SubscriptionManager.setTier(UserTier.ANONYMOUS)
        val result = FirebaseAuthManager.signInWithGoogle("mock_id_token_12345")

        assertTrue(result.isSuccess)
        val state = result.getOrNull()
        assertTrue(state != null)
        assertFalse(state!!.isAnonymous)
        assertTrue(state.isSignedIn)
        assertEquals("Google User", FirebaseAuthManager.displayName)

        // Verifies upgrading from ANONYMOUS to REGISTERED_FREE upon Google sign-in
        assertEquals(UserTier.REGISTERED_FREE, SubscriptionManager.currentTier.value)
    }

    @Test
    fun testSetMockUserState() {
        val mockState = FirebaseUserState(
            uid = "uid_99",
            email = "worship@example.com",
            displayName = "Worship Leader",
            isAnonymous = false,
            isSignedIn = true
        )

        FirebaseAuthManager.setMockUserState(mockState)

        assertEquals("uid_99", FirebaseAuthManager.uid)
        assertEquals("worship@example.com", FirebaseAuthManager.email)
        assertEquals("Worship Leader", FirebaseAuthManager.displayName)
        assertFalse(FirebaseAuthManager.isAnonymous)
        assertTrue(FirebaseAuthManager.isSignedIn)
    }
}
