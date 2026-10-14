package com.example.songchords

import com.example.songchords.auth.UserIdentityManager
import com.example.songchords.firestore.InitializationReport
import com.example.songchords.firestore.ProductionFirestoreInitializer
import com.example.songchords.repository.FirestoreChordLibraryRepository
import com.example.songchords.repository.LocalSetlistRepository
import com.example.songchords.repository.LocalSongRepository
import com.example.songchords.repository.SampleSongs
import com.example.songchords.repository.local.LocalPersistentStorage
import com.example.songchords.subscription.PromoCodeManager
import com.example.songchords.subscription.SubscriptionManager
import com.example.songchords.subscription.UserTier
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ProductionFirestoreInitializerTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var filesDir: File

    @Before
    fun setUp() {
        filesDir = tempFolder.newFolder("filesDir")
        ProductionFirestoreInitializer.clearInMemoryStore()
        PromoCodeManager.resetToDefault()
        SubscriptionManager.resetToDefault()
        UserIdentityManager.resetToDefault()
    }

    @Test
    fun testInitializeSuperAdminUser() = runBlocking {
        val result = ProductionFirestoreInitializer.initializeSuperAdminUser(firestore = null)

        assertTrue(result.success)
        assertEquals(ProductionFirestoreInitializer.COLLECTION_USERS, result.collectionName)
        assertEquals(1, result.count)

        val superAdminMap = ProductionFirestoreInitializer.getSuperAdminUser()
        assertNotNull(superAdminMap)
        assertEquals("SUPERADMIN_FERWINRED", superAdminMap!!["uid"])
        assertEquals("ferwinred@gmail.com", superAdminMap["email"])
        assertEquals("SuperAdmin (ferwinred@gmail.com)", superAdminMap["displayName"])
        assertEquals("SUPER_ADMIN", superAdminMap["role"])
        assertEquals("PREMIUM_AI_SUB", superAdminMap["tier"])
    }

    @Test
    fun testInitializeSongs() = runBlocking {
        val result = ProductionFirestoreInitializer.initializeSongs(firestore = null)

        assertTrue(result.success)
        assertEquals(ProductionFirestoreInitializer.COLLECTION_SONGS, result.collectionName)
        assertEquals(SampleSongs.getSampleSongs().size, result.count)

        val uploadedSongs = ProductionFirestoreInitializer.getSongs()
        assertEquals(6, uploadedSongs.size)

        val tuPoeta = uploadedSongs.find { it.id == "sample_tu_poeta" }
        assertNotNull(tuPoeta)
        assertEquals("Tu Poeta", tuPoeta!!.title)
        assertEquals("Alex Campos", tuPoeta.artist)
        assertEquals(UserIdentityManager.SUPER_ADMIN_ID, tuPoeta.createdByUserId)
    }

    @Test
    fun testInitializeChords() = runBlocking {
        val result = ProductionFirestoreInitializer.initializeChords(firestore = null)

        assertTrue(result.success)
        assertEquals(ProductionFirestoreInitializer.COLLECTION_CHORDS, result.collectionName)
        assertEquals(120, result.count)

        val uploadedChords = ProductionFirestoreInitializer.getChords()
        assertEquals(120, uploadedChords.size)

        val cMajor = uploadedChords["C_major"]
        assertNotNull(cMajor)
        assertEquals("C", cMajor!!.rootNote)
        assertEquals("", cMajor.quality)
        assertEquals("C", cMajor.chordSymbol)

        val aMinor = uploadedChords["A_m"]
        assertNotNull(aMinor)
        assertEquals("A", aMinor!!.rootNote)
        assertEquals("m", aMinor.quality)
        assertEquals("Am", aMinor.chordSymbol)
    }

    @Test
    fun testInitializePromoCodes() = runBlocking {
        val result = ProductionFirestoreInitializer.initializePromoCodes(firestore = null)

        assertTrue(result.success)
        assertEquals(ProductionFirestoreInitializer.COLLECTION_PROMO_CODES, result.collectionName)
        assertEquals(3, result.count)

        val uploadedCodes = ProductionFirestoreInitializer.getPromoCodes()
        assertEquals(3, uploadedCodes.size)

        val vipCode = uploadedCodes["VIP-PRAYER-99"]
        assertNotNull(vipCode)
        assertEquals(UserTier.PREMIUM_AI_SUB, vipCode!!.targetTier)

        val betaProCode = uploadedCodes["BETA-PRO-2026"]
        assertNotNull(betaProCode)
        assertEquals(UserTier.PRO_SUB, betaProCode!!.targetTier)

        val basicFreeCode = uploadedCodes["BASIC-FREE-2026"]
        assertNotNull(basicFreeCode)
        assertEquals(UserTier.BASIC_SUB, basicFreeCode!!.targetTier)
    }

    @Test
    fun testInitializeSetlists() = runBlocking {
        val result = ProductionFirestoreInitializer.initializeSetlists(firestore = null)

        assertTrue(result.success)
        assertEquals(ProductionFirestoreInitializer.COLLECTION_SETLISTS, result.collectionName)
        assertEquals(2, result.count)

        val uploadedSetlists = ProductionFirestoreInitializer.getSetlists()
        assertEquals(2, uploadedSetlists.size)

        val sundaySetlist = uploadedSetlists.find { it.id == "setlist-sunday-1" }
        assertNotNull(sundaySetlist)
        assertEquals("Servicio de Domingo - Adoración", sundaySetlist!!.title)
        assertTrue(sundaySetlist.songs.isNotEmpty())
    }

    @Test
    fun testInitializeAll() = runBlocking {
        var callbackReport: InitializationReport? = null

        val report = ProductionFirestoreInitializer.initializeAll(
            firestore = null,
            onComplete = { callbackReport = it },
        )

        assertEquals(5, report.totalCollections)
        assertEquals(5, report.successfulCollections)
        assertEquals(132, report.totalDocumentsUploaded) // 1 user + 6 songs + 120 chords + 3 promo codes + 2 setlists
        assertTrue(report.isSuperAdminCreated)

        assertNotNull(callbackReport)
        assertEquals(132, callbackReport!!.totalDocumentsUploaded)

        assertNotNull(ProductionFirestoreInitializer.getSuperAdminUser())
        assertEquals(6, ProductionFirestoreInitializer.getSongs().size)
        assertEquals(120, ProductionFirestoreInitializer.getChords().size)
        assertEquals(3, ProductionFirestoreInitializer.getPromoCodes().size)
        assertEquals(2, ProductionFirestoreInitializer.getSetlists().size)

        assertTrue(UserIdentityManager.isFirestoreSeeded())
    }

    @Test
    fun testInitializeAll_populatesLocalDeviceStorage() = runBlocking {
        val persistentStorage = LocalPersistentStorage(filesDir)
        val songRepository = LocalSongRepository(persistentStorage = persistentStorage)
        val setlistRepository = LocalSetlistRepository(persistentStorage = persistentStorage)
        val chordRepository = FirestoreChordLibraryRepository()

        val report = ProductionFirestoreInitializer.initializeAll(
            firestore = null,
            localSongRepository = songRepository,
            localSetlistRepository = setlistRepository,
            chordLibraryRepository = chordRepository,
            localPersistentStorage = persistentStorage,
        )

        assertEquals(132, report.totalDocumentsUploaded)

        // 1. SuperAdmin User (1 doc)
        assertNotNull(ProductionFirestoreInitializer.getSuperAdminUser())

        // 2. Catalog Songs (6 docs in LocalSongRepository & LocalPersistentStorage)
        val songsFromRepo = songRepository.getSongs().first()
        assertEquals(6, songsFromRepo.size)
        val songsFromStorage = persistentStorage.loadLocalSongs()
        assertEquals(6, songsFromStorage.size)

        // 3. Chord Diagrams (120 docs in FirestoreChordLibraryRepository)
        assertEquals(120, chordRepository.getFirestoreCollection().size)

        // 4. Promo Codes (3 docs in PromoCodeManager)
        assertEquals(3, ProductionFirestoreInitializer.getPromoCodes().size)

        // 5. Worship Setlists (2 docs in LocalSetlistRepository & LocalPersistentStorage)
        val setlistsFromRepo = setlistRepository.getSetlists().first()
        assertEquals(2, setlistsFromRepo.size)
        val setlistsFromStorage = persistentStorage.loadLocalSetlists()
        assertEquals(2, setlistsFromStorage.size)
    }
}
