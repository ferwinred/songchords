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
import com.example.songchords.repository.SampleSongs
import com.example.songchords.subscription.PromoCode
import com.example.songchords.subscription.PromoCodeManager
import com.example.songchords.subscription.UserTier
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Execution result status for a single Cloud Firestore collection seeding operation.
 */
data class SeedResult(
    val collectionName: String,
    val success: Boolean,
    val count: Int,
    val message: String = "",
)

/**
 * Aggregated report detailing the overall outcome across all seeded Firestore collections.
 */
data class SeedingReport(
    val totalCollections: Int,
    val successfulCollections: Int,
    val totalDocumentsSeeded: Int,
    val results: List<SeedResult>,
)

/**
 * Automated Firebase Firestore Seeder Engine.
 *
 * Populates and synchronizes Cloud Firestore collections:
 *  1. 'songs': SuperAdmin catalog songs from [SampleSongs.getSampleSongs].
 *  2. 'chords': 120 Guitar & Piano chord entries across 12 root notes and 10 chord qualities.
 *  3. 'promo_codes': Single-use promotional codes granting user subscription tiers.
 *  4. 'setlists': Sample worship setlists.
 */
object FirestoreSeeder {

    const val COLLECTION_SONGS = "songs"
    const val COLLECTION_CHORDS = "chords"
    const val COLLECTION_PROMO_CODES = "promo_codes"
    const val COLLECTION_SETLISTS = "setlists"

    // In-memory stores supporting offline operation and unit testing verification
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

    private suspend fun handleFirestoreError(context: Context?, e: Throwable) {
        val message = when (e) {
            is FirebaseFirestoreException -> {
                when (e.code) {
                    FirebaseFirestoreException.Code.NOT_FOUND ->
                        "Error Firestore: La base de datos no existe o debe habilitarse en Firebase Console."
                    FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                        "Error Firestore: Permiso denegado. Verifica las reglas en Firebase Console."
                    FirebaseFirestoreException.Code.UNAVAILABLE ->
                        "Error Firestore: Servicio no disponible o sin conexión a internet."
                    else ->
                        "Error Firestore (${e.code.name}): ${e.message}"
                }
            }
            else -> "Error en Firestore: ${e.message ?: e.toString()}"
        }

        context?.let { ctx ->
            withContext(Dispatchers.Main) {
                Toast.makeText(ctx, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Populates 'songs' collection in Cloud Firestore with SuperAdmin catalog songs.
     */
    suspend fun seedSongsCollection(
        firestore: FirebaseFirestore? = getFirestore(),
        cloudProvider: CloudSyncProvider? = null,
        context: Context? = null,
    ): SeedResult {
        var count = 0
        var lastError: Throwable? = null
        val songs = SampleSongs.getSampleSongs()

        for (song in songs) {
            inMemorySongs[song.id] = song
            cloudProvider?.saveRemoteSong(song)

            if (firestore != null) {
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
                    count++
                } catch (e: FirebaseFirestoreException) {
                    lastError = e
                    handleFirestoreError(context, e)
                    break
                } catch (e: Throwable) {
                    lastError = e
                    handleFirestoreError(context, e)
                    break
                }
            } else {
                count++
            }
        }

        val isSuccess = lastError == null
        return SeedResult(
            collectionName = COLLECTION_SONGS,
            success = isSuccess,
            count = count,
            message = if (isSuccess) "Successfully seeded $count songs into '$COLLECTION_SONGS' collection." else "Error en '$COLLECTION_SONGS': ${lastError?.message.orEmpty()}",
        )
    }

    /**
     * Populates 'chords' collection in Cloud Firestore with 120 Guitar & Piano chord entries
     * across 12 root notes and 10 chord qualities.
     */
    suspend fun seedChordsCollection(
        firestore: FirebaseFirestore? = getFirestore(),
        context: Context? = null,
    ): SeedResult {
        var count = 0
        var lastError: Throwable? = null
        val rootNotes = FirestoreChordLibraryRepository.ROOT_NOTES
        val qualities = FirestoreChordLibraryRepository.QUALITIES

        for (root in rootNotes) {
            if (lastError != null) break
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

                inMemoryChords[docId] = doc

                if (firestore != null) {
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
                        firestore.collection(COLLECTION_CHORDS).document(docId).set(data).await()
                        count++
                    } catch (e: FirebaseFirestoreException) {
                        lastError = e
                        handleFirestoreError(context, e)
                        break
                    } catch (e: Throwable) {
                        lastError = e
                        handleFirestoreError(context, e)
                        break
                    }
                } else {
                    count++
                }
            }
        }

        val isSuccess = lastError == null
        return SeedResult(
            collectionName = COLLECTION_CHORDS,
            success = isSuccess,
            count = count,
            message = if (isSuccess) "Successfully seeded $count chord entries into '$COLLECTION_CHORDS' collection." else "Error en '$COLLECTION_CHORDS': ${lastError?.message}",
        )
    }

    /**
     * Populates 'promo_codes' collection in Cloud Firestore with single-use promotional codes.
     */
    suspend fun seedPromoCodesCollection(
        firestore: FirebaseFirestore? = getFirestore(),
        context: Context? = null,
    ): SeedResult {
        val promoCodes = listOf(
            PromoCode("VIP-PRAYER-99", UserTier.PREMIUM_AI_SUB),
            PromoCode("BETA-PRO-2026", UserTier.PRO_SUB),
            PromoCode("BASIC-FREE-2026", UserTier.BASIC_SUB),
        )

        var count = 0
        var lastError: Throwable? = null

        for (promo in promoCodes) {
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
                    count++
                } catch (e: FirebaseFirestoreException) {
                    lastError = e
                    handleFirestoreError(context, e)
                    break
                } catch (e: Throwable) {
                    lastError = e
                    handleFirestoreError(context, e)
                    break
                }
            } else {
                count++
            }
        }

        val isSuccess = lastError == null
        return SeedResult(
            collectionName = COLLECTION_PROMO_CODES,
            success = isSuccess,
            count = count,
            message = if (isSuccess) "Successfully seeded $count promo codes into '$COLLECTION_PROMO_CODES' collection." else "Error en '$COLLECTION_PROMO_CODES': ${lastError?.message}",
        )
    }

    /**
     * Populates 'setlists' collection in Cloud Firestore with sample worship setlists.
     */
    suspend fun seedSetlistsCollection(
        firestore: FirebaseFirestore? = getFirestore(),
        context: Context? = null,
    ): SeedResult {
        val sampleSetlists = LocalSetlistRepository.createSampleSetlists()
        var count = 0
        var lastError: Throwable? = null

        for (setlist in sampleSetlists) {
            inMemorySetlists[setlist.id] = setlist

            if (firestore != null) {
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
                    count++
                } catch (e: FirebaseFirestoreException) {
                    lastError = e
                    handleFirestoreError(context, e)
                    break
                } catch (e: Throwable) {
                    lastError = e
                    handleFirestoreError(context, e)
                    break
                }
            } else {
                count++
            }
        }

        val isSuccess = lastError == null
        return SeedResult(
            collectionName = COLLECTION_SETLISTS,
            success = isSuccess,
            count = count,
            message = if (isSuccess) "Successfully seeded $count setlists into '$COLLECTION_SETLISTS' collection." else "Error en '$COLLECTION_SETLISTS': ${lastError?.message}",
        )
    }

    /**
     * Seeds all 4 Cloud Firestore collections sequentially with aggregated result status tracking.
     */
    suspend fun seedAllCollections(
        firestore: FirebaseFirestore? = getFirestore(),
        cloudProvider: CloudSyncProvider? = null,
        context: Context? = null,
    ): SeedingReport {
        val results = listOf(
            seedSongsCollection(firestore, cloudProvider, context),
            seedChordsCollection(firestore, context),
            seedPromoCodesCollection(firestore, context),
            seedSetlistsCollection(firestore, context),
        )

        val totalCollections = results.size
        val successfulCollections = results.count { it.success }
        val totalDocumentsSeeded = results.sumOf { it.count }

        if (successfulCollections == totalCollections) {
            UserIdentityManager.markFirestoreSeeded()
        }

        return SeedingReport(
            totalCollections = totalCollections,
            successfulCollections = successfulCollections,
            totalDocumentsSeeded = totalDocumentsSeeded,
            results = results,
        )
    }

    fun getSeededSongs(): List<Song> = inMemorySongs.values.toList()
    fun getSeededChords(): Map<String, ChordDiagramDocument> = inMemoryChords.toMap()
    fun getSeededPromoCodes(): Map<String, PromoCode> = inMemoryPromoCodes.toMap()
    fun getSeededSetlists(): List<Setlist> = inMemorySetlists.values.toList()

    fun clearInMemoryStore() {
        inMemorySongs.clear()
        inMemoryChords.clear()
        inMemoryPromoCodes.clear()
        inMemorySetlists.clear()
    }
}
