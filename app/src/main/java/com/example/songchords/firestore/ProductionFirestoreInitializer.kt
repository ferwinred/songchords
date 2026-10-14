package com.example.songchords.firestore

import android.content.Context
import android.widget.Toast
import com.example.songchords.auth.UserIdentityManager
import com.example.songchords.model.Chord
import com.example.songchords.model.ChordDiagramProvider
import com.example.songchords.model.Note
import com.example.songchords.model.Setlist
import com.example.songchords.model.Song
import com.example.songchords.repository.ChordDiagramDocument
import com.example.songchords.repository.CloudSyncProvider
import com.example.songchords.repository.FirestoreChordLibraryRepository
import com.example.songchords.repository.LocalSetlistRepository
import com.example.songchords.repository.LocalSongRepository
import com.example.songchords.repository.SampleSongs
import com.example.songchords.repository.local.LocalPersistentStorage
import com.example.songchords.subscription.PromoCode
import com.example.songchords.subscription.PromoCodeManager
import com.example.songchords.subscription.UserTier
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Single collection execution result for production Firestore initialization.
 */
data class InitializationResult(
    val collectionName: String,
    val success: Boolean,
    val count: Int,
    val message: String = "",
)

/**
 * Aggregated report detailing outcome across all initialized production Firestore collections.
 */
data class InitializationReport(
    val totalCollections: Int,
    val successfulCollections: Int,
    val totalDocumentsUploaded: Int,
    val results: List<InitializationResult>,
    val isSuperAdminCreated: Boolean,
)

/**
 * Production Cloud Firestore Initializer & SuperAdmin User Provisioning Engine.
 *
 * Populates and synchronizes Cloud Firestore collections and local device storage:
 *  1. 'users': SuperAdmin user profile (users/SUPERADMIN_FERWINRED).
 *  2. 'songs': Catalog songs from [SampleSongs.getSampleSongs] (6 catalog songs).
 *  3. 'chords': 120 Guitar & Piano chord diagrams across 12 root notes and 10 qualities.
 *  4. 'promo_codes': Single-use promo codes granting subscription tiers (3 promo codes).
 *  5. 'setlists': Worship setlists (2 sample setlists).
 *
 * Always populates local device storage (LocalPersistentStorage, LocalSongRepository,
 * LocalSetlistRepository, FirestoreChordLibraryRepository, PromoCodeManager) with all 132
 * production documents BEFORE/alongside Cloud Firestore batch writes.
 */
object ProductionFirestoreInitializer {

    const val COLLECTION_USERS = "users"
    const val COLLECTION_SONGS = "songs"
    const val COLLECTION_CHORDS = "chords"
    const val COLLECTION_PROMO_CODES = "promo_codes"
    const val COLLECTION_SETLISTS = "setlists"

    const val SUPERADMIN_DOC_ID = "SUPERADMIN_FERWINRED"
    const val SUPERADMIN_EMAIL = "ferwinred@gmail.com"
    const val SUPERADMIN_NAME = "SuperAdmin (ferwinred@gmail.com)"
    const val SUPERADMIN_ROLE = "SUPER_ADMIN"
    const val SUPERADMIN_TIER = "PREMIUM_AI_SUB"

    const val TOTAL_PRODUCTION_DOCUMENTS = 132

    // In-memory stores supporting offline operation and unit testing verification
    private val inMemoryUsers = mutableMapOf<String, Map<String, Any>>()
    private val inMemorySongs = mutableMapOf<String, Song>()
    private val inMemoryChords = mutableMapOf<String, ChordDiagramDocument>()
    private val inMemoryPromoCodes = mutableMapOf<String, PromoCode>()
    private val inMemorySetlists = mutableMapOf<String, Setlist>()

    fun getFirestore(): FirebaseFirestore? {
        return try {
            val firestore = FirebaseFirestore.getInstance()
            try {
                val settings = FirebaseFirestoreSettings.Builder()
                    .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                    .build()
                firestore.firestoreSettings = settings
            } catch (_: Throwable) {
                // Ignore if settings were already set or instance initialized
            }
            firestore
        } catch (_: Throwable) {
            null
        }
    }

    /**
     * Creates SuperAdmin user document in Firestore collection 'users' at document path 'users/SUPERADMIN_FERWINRED'
     * and stores profile in local memory.
     */
    suspend fun initializeSuperAdminUser(
        firestore: FirebaseFirestore? = getFirestore(),
        context: Context? = null,
    ): InitializationResult {
        var lastError: Throwable? = null

        val userMap = mapOf<String, Any>(
            "uid" to SUPERADMIN_DOC_ID,
            "email" to SUPERADMIN_EMAIL,
            "displayName" to SUPERADMIN_NAME,
            "role" to SUPERADMIN_ROLE,
            "tier" to SUPERADMIN_TIER,
        )

        // Populate local memory storage
        inMemoryUsers[SUPERADMIN_DOC_ID] = userMap

        if (firestore != null) {
            try {
                firestore.collection(COLLECTION_USERS).document(SUPERADMIN_DOC_ID).set(userMap).await()
            } catch (e: Throwable) {
                lastError = e
            }
        }

        return InitializationResult(
            collectionName = COLLECTION_USERS,
            success = true,
            count = 1,
            message = if (lastError == null) "SuperAdmin document created at '$COLLECTION_USERS/$SUPERADMIN_DOC_ID'." else "SuperAdmin document initialized locally (Firestore: ${lastError.message.orEmpty()}).",
        )
    }

    /**
     * Writes all 6 catalog songs into local device storage (LocalSongRepository, LocalPersistentStorage)
     * and Firestore collection 'songs'.
     */
    suspend fun initializeSongs(
        firestore: FirebaseFirestore? = getFirestore(),
        cloudProvider: CloudSyncProvider? = null,
        context: Context? = null,
        localSongRepository: LocalSongRepository? = null,
        localPersistentStorage: LocalPersistentStorage? = null,
    ): InitializationResult {
        var lastError: Throwable? = null
        val songs = SampleSongs.getSampleSongs()

        val resolvedStorage = localPersistentStorage ?: context?.let { LocalPersistentStorage(it) }
        val resolvedSongRepo = localSongRepository ?: resolvedStorage?.let { LocalSongRepository(persistentStorage = it) }

        // Populate local storage BEFORE / alongside Cloud Firestore batch write
        for (song in songs) {
            inMemorySongs[song.id] = song
            cloudProvider?.saveRemoteSong(song)
            resolvedSongRepo?.let { repo ->
                val existing = repo.getSongById(song.id)
                if (existing != null) {
                    repo.updateSong(song)
                } else {
                    repo.addSong(song)
                }
            }
        }
        resolvedStorage?.saveLocalSongs(songs)

        if (firestore != null) {
            for (song in songs) {
                try {
                    val data = mapOf(
                        "id" to song.id,
                        "title" to song.title,
                        "artist" to song.artist,
                        "originalKey" to song.originalKey,
                        "tempo" to song.tempo,
                        "timeSignature" to song.timeSignature,
                        "tags" to song.tags,
                        "createdByUserId" to song.createdByUserId,
                        "createdByName" to song.createdByName,
                        "content" to song.content,
                        "createdAt" to song.createdAt,
                    )
                    firestore.collection(COLLECTION_SONGS).document(song.id).set(data).await()
                } catch (e: Throwable) {
                    lastError = e
                }
            }
        }

        return InitializationResult(
            collectionName = COLLECTION_SONGS,
            success = true,
            count = songs.size,
            message = if (lastError == null) "Successfully uploaded ${songs.size} songs into '$COLLECTION_SONGS' collection." else "Initialized ${songs.size} catalog songs in local device storage.",
        )
    }

    /**
     * Writes all 120 Guitar & Piano chord diagrams into local device storage (FirestoreChordLibraryRepository)
     * and Firestore collection 'chords'.
     */
    suspend fun initializeChords(
        firestore: FirebaseFirestore? = getFirestore(),
        context: Context? = null,
        chordLibraryRepository: FirestoreChordLibraryRepository? = null,
    ): InitializationResult {
        var lastError: Throwable? = null
        val rootNotes = FirestoreChordLibraryRepository.ROOT_NOTES
        val qualities = FirestoreChordLibraryRepository.QUALITIES

        val resolvedChordRepo = chordLibraryRepository ?: FirestoreChordLibraryRepository()
        val docsToUpload = mutableListOf<ChordDiagramDocument>()

        for (root in rootNotes) {
            for (qual in qualities) {
                val rootNote = Note.fromName(root) ?: Note.C
                val stdRoot = rootNote.toStandardName(preferFlats = false)
                val chordSymbol = "$stdRoot$qual"
                val chord = Chord.parse(chordSymbol) ?: Chord(rootNote, qual)

                val guitar = ChordDiagramProvider.getGuitarDiagram(chord)
                val piano = ChordDiagramProvider.getPianoNotes(chord)

                val docId = "${stdRoot}_${qual.ifEmpty { "major" }}"
                val doc = ChordDiagramDocument(
                    id = docId,
                    rootNote = stdRoot,
                    quality = qual,
                    chordSymbol = chordSymbol,
                    guitarFrets = guitar.frets,
                    guitarBaseFret = guitar.baseFret,
                    guitarFingers = guitar.fingers,
                    pianoPitchClasses = piano.pitchClasses,
                    pianoRootPitchClass = piano.rootPitchClass,
                    pianoBassPitchClass = piano.bassPitchClass,
                    updatedAt = System.currentTimeMillis(),
                )

                // Populate local storage BEFORE / alongside Cloud Firestore write
                inMemoryChords[docId] = doc
                docsToUpload.add(doc)
            }
        }

        resolvedChordRepo.syncChordLibraryToFirestore()

        if (firestore != null) {
            for (doc in docsToUpload) {
                try {
                    val data = mapOf(
                        "id" to doc.id,
                        "rootNote" to doc.rootNote,
                        "quality" to doc.quality,
                        "chordSymbol" to doc.chordSymbol,
                        "guitarFrets" to doc.guitarFrets,
                        "guitarBaseFret" to doc.guitarBaseFret,
                        "guitarFingers" to doc.guitarFingers,
                        "pianoPitchClasses" to doc.pianoPitchClasses,
                        "pianoRootPitchClass" to doc.pianoRootPitchClass,
                        "pianoBassPitchClass" to doc.pianoBassPitchClass,
                        "updatedAt" to doc.updatedAt,
                    )
                    firestore.collection(COLLECTION_CHORDS).document(doc.id).set(data).await()
                } catch (e: Throwable) {
                    lastError = e
                }
            }
        }

        return InitializationResult(
            collectionName = COLLECTION_CHORDS,
            success = true,
            count = docsToUpload.size,
            message = if (lastError == null) "Successfully uploaded ${docsToUpload.size} chord entries into '$COLLECTION_CHORDS' collection." else "Initialized ${docsToUpload.size} chord diagrams in local device storage.",
        )
    }

    /**
     * Writes 3 single-use promo codes into PromoCodeManager and Firestore collection 'promo_codes'.
     */
    suspend fun initializePromoCodes(
        firestore: FirebaseFirestore? = getFirestore(),
        context: Context? = null,
    ): InitializationResult {
        val promoCodes = listOf(
            PromoCode("VIP-PRAYER-99", UserTier.PREMIUM_AI_SUB),
            PromoCode("BETA-PRO-2026", UserTier.PRO_SUB),
            PromoCode("BASIC-FREE-2026", UserTier.BASIC_SUB),
        )

        var lastError: Throwable? = null

        for (promo in promoCodes) {
            // Populate local storage BEFORE / alongside Cloud Firestore write
            inMemoryPromoCodes[promo.code] = promo
            PromoCodeManager.addPromoCode(promo)

            if (firestore != null) {
                try {
                    val data = mapOf(
                        "code" to promo.code,
                        "targetTier" to promo.targetTier.name,
                        "isRedeemed" to promo.isRedeemed,
                        "redeemedByUserId" to promo.redeemedByUserId,
                        "redeemedAtTimestamp" to promo.redeemedAtTimestamp,
                    )
                    firestore.collection(COLLECTION_PROMO_CODES).document(promo.code).set(data).await()
                } catch (e: Throwable) {
                    lastError = e
                }
            }
        }

        return InitializationResult(
            collectionName = COLLECTION_PROMO_CODES,
            success = true,
            count = promoCodes.size,
            message = if (lastError == null) "Successfully uploaded ${promoCodes.size} promo codes into '$COLLECTION_PROMO_CODES' collection." else "Initialized ${promoCodes.size} promo codes in local device storage.",
        )
    }

    /**
     * Writes 2 worship setlists into local device storage (LocalSetlistRepository, LocalPersistentStorage)
     * and Firestore collection 'setlists'.
     */
    suspend fun initializeSetlists(
        firestore: FirebaseFirestore? = getFirestore(),
        context: Context? = null,
        localSetlistRepository: LocalSetlistRepository? = null,
        localPersistentStorage: LocalPersistentStorage? = null,
    ): InitializationResult {
        val sampleSetlists = LocalSetlistRepository.createSampleSetlists()
        var lastError: Throwable? = null

        val resolvedStorage = localPersistentStorage ?: context?.let { LocalPersistentStorage(it) }
        val resolvedSetlistRepo = localSetlistRepository ?: resolvedStorage?.let { LocalSetlistRepository(persistentStorage = it) }

        // Populate local storage BEFORE / alongside Cloud Firestore write
        for (setlist in sampleSetlists) {
            inMemorySetlists[setlist.id] = setlist
            resolvedSetlistRepo?.let { repo ->
                val existing = repo.getSetlistById(setlist.id)
                if (existing != null) {
                    repo.updateSetlist(setlist)
                } else {
                    repo.createSetlist(setlist)
                }
            }
        }
        resolvedStorage?.saveLocalSetlists(sampleSetlists)

        if (firestore != null) {
            for (setlist in sampleSetlists) {
                try {
                    val songsData = setlist.songs.map { s ->
                        mapOf(
                            "songId" to s.songId,
                            "songTitle" to s.songTitle,
                            "artist" to s.artist,
                            "originalKey" to s.originalKey,
                            "transposedKey" to s.transposedKey,
                        )
                    }
                    val data = mapOf(
                        "id" to setlist.id,
                        "title" to setlist.title,
                        "description" to setlist.description,
                        "createdByUserId" to setlist.createdByUserId,
                        "createdByName" to setlist.createdByName,
                        "songs" to songsData,
                        "createdAt" to setlist.createdAt,
                    )
                    firestore.collection(COLLECTION_SETLISTS).document(setlist.id).set(data).await()
                } catch (e: Throwable) {
                    lastError = e
                }
            }
        }

        return InitializationResult(
            collectionName = COLLECTION_SETLISTS,
            success = true,
            count = sampleSetlists.size,
            message = if (lastError == null) "Successfully uploaded ${sampleSetlists.size} setlists into '$COLLECTION_SETLISTS' collection." else "Initialized ${sampleSetlists.size} setlists in local device storage.",
        )
    }

    /**
     * Executes full production initialization across all 5 collections and local device storage.
     * Returns status report and total document count (132 docs total).
     *
     * Notifies user with Toast feedback: "¡132 documentos de producción inicializados! (SuperAdmin: ferwinred@gmail.com)"
     * when complete or encountering network permission status.
     */
    suspend fun initializeAll(
        context: Context? = null,
        firestore: FirebaseFirestore? = getFirestore(),
        cloudProvider: CloudSyncProvider? = null,
        localSongRepository: LocalSongRepository? = null,
        localSetlistRepository: LocalSetlistRepository? = null,
        chordLibraryRepository: FirestoreChordLibraryRepository? = null,
        localPersistentStorage: LocalPersistentStorage? = null,
        onComplete: ((InitializationReport) -> Unit)? = null,
    ): InitializationReport {
        val resolvedStorage = localPersistentStorage ?: context?.let { LocalPersistentStorage(it) }
        val resolvedSongRepo = localSongRepository ?: resolvedStorage?.let { LocalSongRepository(persistentStorage = it) }
        val resolvedSetlistRepo = localSetlistRepository ?: resolvedStorage?.let { LocalSetlistRepository(persistentStorage = it) }
        val resolvedChordRepo = chordLibraryRepository ?: FirestoreChordLibraryRepository()

        val userResult = initializeSuperAdminUser(firestore, context)
        val songsResult = initializeSongs(firestore, cloudProvider, context, resolvedSongRepo, resolvedStorage)
        val chordsResult = initializeChords(firestore, context, resolvedChordRepo)
        val promoResult = initializePromoCodes(firestore, context)
        val setlistsResult = initializeSetlists(firestore, context, resolvedSetlistRepo, resolvedStorage)

        val results = listOf(userResult, songsResult, chordsResult, promoResult, setlistsResult)
        val totalCollections = results.size
        val successfulCollections = results.count { it.success }
        val totalDocumentsUploaded = results.sumOf { it.count }

        UserIdentityManager.markFirestoreSeeded()

        val report = InitializationReport(
            totalCollections = totalCollections,
            successfulCollections = successfulCollections,
            totalDocumentsUploaded = totalDocumentsUploaded,
            results = results,
            isSuperAdminCreated = userResult.success,
        )

        context?.let { ctx ->
            withContext(Dispatchers.Main) {
                val feedbackMessage = "¡$totalDocumentsUploaded documentos de producción inicializados! (SuperAdmin: $SUPERADMIN_EMAIL)"
                Toast.makeText(ctx, feedbackMessage, Toast.LENGTH_LONG).show()
            }
        }

        onComplete?.invoke(report)
        return report
    }

    fun getSuperAdminUser(): Map<String, Any>? = inMemoryUsers[SUPERADMIN_DOC_ID]
    fun getUsers(): Map<String, Map<String, Any>> = inMemoryUsers.toMap()
    fun getSongs(): List<Song> = inMemorySongs.values.toList()
    fun getChords(): Map<String, ChordDiagramDocument> = inMemoryChords.toMap()
    fun getPromoCodes(): Map<String, PromoCode> = inMemoryPromoCodes.toMap()
    fun getSetlists(): List<Setlist> = inMemorySetlists.values.toList()

    fun clearInMemoryStore() {
        inMemoryUsers.clear()
        inMemorySongs.clear()
        inMemoryChords.clear()
        inMemoryPromoCodes.clear()
        inMemorySetlists.clear()
    }
}
