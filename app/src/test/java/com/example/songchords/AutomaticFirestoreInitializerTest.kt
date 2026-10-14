package com.example.songchords

import com.example.songchords.auth.UserIdentityManager
import com.example.songchords.firestore.AutomaticFirestoreInitializer
import com.example.songchords.firestore.ProductionFirestoreInitializer
import com.example.songchords.subscription.PromoCodeManager
import com.example.songchords.subscription.SubscriptionManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AutomaticFirestoreInitializerTest {

    @Before
    fun setUp() {
        AutomaticFirestoreInitializer.resetForTesting()
        PromoCodeManager.resetToDefault()
        SubscriptionManager.resetToDefault()
        UserIdentityManager.resetToDefault()
    }

    @Test
    fun testCheckAndSeedIfEmpty_whenEmpty_seedsAll5Collections() = runBlocking {
        val seeded = AutomaticFirestoreInitializer.checkAndSeedIfEmpty(
            context = null,
            firestore = null,
            isDbEmptyOverride = true,
        )

        assertTrue(seeded)

        // Verify all 5 collections are populated
        // 1. users/SUPERADMIN_FERWINRED
        val superAdminUser = ProductionFirestoreInitializer.getSuperAdminUser()
        assertNotNull(superAdminUser)
        assertEquals("SUPERADMIN_FERWINRED", superAdminUser!!["uid"])
        assertEquals("ferwinred@gmail.com", superAdminUser["email"])
        assertEquals("SUPER_ADMIN", superAdminUser["role"])

        // 2. songs
        val songs = ProductionFirestoreInitializer.getSongs()
        assertEquals(6, songs.size)

        // 3. chords
        val chords = ProductionFirestoreInitializer.getChords()
        assertEquals(120, chords.size)

        // 4. promo_codes
        val promoCodes = ProductionFirestoreInitializer.getPromoCodes()
        assertEquals(3, promoCodes.size)

        // 5. setlists
        val setlists = ProductionFirestoreInitializer.getSetlists()
        assertEquals(2, setlists.size)

        // Total documents = 1 + 6 + 120 + 3 + 2 = 132
        val totalDocs = 1 + songs.size + chords.size + promoCodes.size + setlists.size
        assertEquals(132, totalDocs)
    }

    @Test
    fun testCheckAndSeedIfEmpty_whenNotEmpty_skipsSeedingToGuaranteeZeroDuplication() = runBlocking {
        // First seed database
        AutomaticFirestoreInitializer.checkAndSeedIfEmpty(
            context = null,
            firestore = null,
            isDbEmptyOverride = true,
        )

        val initialSongCount = ProductionFirestoreInitializer.getSongs().size
        assertEquals(6, initialSongCount)

        // Now attempt seeding when database is NOT empty
        val seededSecondTime = AutomaticFirestoreInitializer.checkAndSeedIfEmpty(
            context = null,
            firestore = null,
            isDbEmptyOverride = false,
        )

        assertFalse(seededSecondTime)
        // Song count must remain unchanged to guarantee ZERO DUPLICATION
        assertEquals(initialSongCount, ProductionFirestoreInitializer.getSongs().size)
    }

    @Test
    fun testCheckAndSeedIfEmpty_consecutiveCalls_skipsSecondRun() = runBlocking {
        val firstRun = AutomaticFirestoreInitializer.checkAndSeedIfEmpty(
            context = null,
            firestore = null,
        )

        assertTrue(firstRun)
        assertEquals(6, ProductionFirestoreInitializer.getSongs().size)

        // Second run when database is no longer empty
        val secondRun = AutomaticFirestoreInitializer.checkAndSeedIfEmpty(
            context = null,
            firestore = null,
        )

        assertFalse(secondRun)
        // Verify document counts remained unchanged (zero duplication)
        assertEquals(6, ProductionFirestoreInitializer.getSongs().size)
        assertEquals(120, ProductionFirestoreInitializer.getChords().size)
        assertEquals(3, ProductionFirestoreInitializer.getPromoCodes().size)
        assertEquals(2, ProductionFirestoreInitializer.getSetlists().size)
    }

    @Test
    fun testCollectionConstantsAndSuperAdminProfile() {
        assertEquals("songs", AutomaticFirestoreInitializer.COLLECTION_SONGS)
        assertEquals("users", AutomaticFirestoreInitializer.COLLECTION_USERS)
        assertEquals("chords", AutomaticFirestoreInitializer.COLLECTION_CHORDS)
        assertEquals("promo_codes", AutomaticFirestoreInitializer.COLLECTION_PROMO_CODES)
        assertEquals("setlists", AutomaticFirestoreInitializer.COLLECTION_SETLISTS)
    }
}
