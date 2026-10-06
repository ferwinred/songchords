package com.example.songchords

import com.example.songchords.auth.UserIdentityManager
import com.example.songchords.firestore.FirestoreSeeder
import com.example.songchords.repository.SampleSongs
import com.example.songchords.subscription.PromoCodeManager
import com.example.songchords.subscription.SubscriptionManager
import com.example.songchords.subscription.UserTier
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FirestoreSeederTest {

    @Before
    fun setUp() {
        FirestoreSeeder.clearInMemoryStore()
        PromoCodeManager.resetToDefault()
        SubscriptionManager.resetToDefault()
    }

    @Test
    fun testSeedSongsCollection() = runBlocking {
        val result = FirestoreSeeder.seedSongsCollection()

        assertTrue(result.success)
        assertEquals(FirestoreSeeder.COLLECTION_SONGS, result.collectionName)
        assertEquals(SampleSongs.getSampleSongs().size, result.count)

        val seededSongs = FirestoreSeeder.getSeededSongs()
        assertEquals(6, seededSongs.size)

        val tuPoeta = seededSongs.find { it.id == "sample_tu_poeta" }
        assertNotNull(tuPoeta)
        assertEquals("Tu Poeta", tuPoeta!!.title)
        assertEquals("Alex Campos", tuPoeta.artist)
        assertEquals(UserIdentityManager.SUPER_ADMIN_ID, tuPoeta.createdByUserId)
    }

    @Test
    fun testSeedChordsCollection() = runBlocking {
        val result = FirestoreSeeder.seedChordsCollection()

        assertTrue(result.success)
        assertEquals(FirestoreSeeder.COLLECTION_CHORDS, result.collectionName)
        assertEquals(120, result.count)

        val seededChords = FirestoreSeeder.getSeededChords()
        assertEquals(120, seededChords.size)

        val cMajor = seededChords["C_major"]
        assertNotNull(cMajor)
        assertEquals("C", cMajor!!.rootNote)
        assertEquals("", cMajor.quality)
        assertEquals("C", cMajor.chordSymbol)

        val aMinor = seededChords["A_m"]
        assertNotNull(aMinor)
        assertEquals("A", aMinor!!.rootNote)
        assertEquals("m", aMinor.quality)
        assertEquals("Am", aMinor.chordSymbol)
    }

    @Test
    fun testSeedPromoCodesCollection() = runBlocking {
        val result = FirestoreSeeder.seedPromoCodesCollection()

        assertTrue(result.success)
        assertEquals(FirestoreSeeder.COLLECTION_PROMO_CODES, result.collectionName)
        assertEquals(3, result.count)

        val seededCodes = FirestoreSeeder.getSeededPromoCodes()
        assertEquals(3, seededCodes.size)

        val vipCode = seededCodes["VIP-PRAYER-99"]
        assertNotNull(vipCode)
        assertEquals(UserTier.PREMIUM_AI_SUB, vipCode!!.targetTier)

        val betaProCode = seededCodes["BETA-PRO-2026"]
        assertNotNull(betaProCode)
        assertEquals(UserTier.PRO_SUB, betaProCode!!.targetTier)

        val basicFreeCode = seededCodes["BASIC-FREE-2026"]
        assertNotNull(basicFreeCode)
        assertEquals(UserTier.BASIC_SUB, basicFreeCode!!.targetTier)

        // Test redemption of seeded promo code
        val redeemResult = PromoCodeManager.redeemPromoCode("VIP-PRAYER-99", "user_test_seeder")
        assertTrue(redeemResult.isSuccess)
        assertEquals(UserTier.PREMIUM_AI_SUB, SubscriptionManager.currentTier.value)
    }

    @Test
    fun testSeedSetlistsCollection() = runBlocking {
        val result = FirestoreSeeder.seedSetlistsCollection()

        assertTrue(result.success)
        assertEquals(FirestoreSeeder.COLLECTION_SETLISTS, result.collectionName)
        assertEquals(2, result.count)

        val seededSetlists = FirestoreSeeder.getSeededSetlists()
        assertEquals(2, seededSetlists.size)

        val sundaySetlist = seededSetlists.find { it.id == "setlist-sunday-1" }
        assertNotNull(sundaySetlist)
        assertEquals("Servicio de Domingo - Adoración", sundaySetlist!!.title)
        assertTrue(sundaySetlist.songs.isNotEmpty())
    }

    @Test
    fun testSeedAllCollections() = runBlocking {
        val report = FirestoreSeeder.seedAllCollections()

        assertEquals(4, report.totalCollections)
        assertEquals(4, report.successfulCollections)
        assertEquals(131, report.totalDocumentsSeeded) // 6 songs + 120 chords + 3 promo codes + 2 setlists

        assertEquals(6, FirestoreSeeder.getSeededSongs().size)
        assertEquals(120, FirestoreSeeder.getSeededChords().size)
        assertEquals(3, FirestoreSeeder.getSeededPromoCodes().size)
        assertEquals(2, FirestoreSeeder.getSeededSetlists().size)
    }
}
