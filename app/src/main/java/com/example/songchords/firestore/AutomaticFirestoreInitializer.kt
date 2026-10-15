package com.example.songchords.firestore

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import kotlinx.coroutines.tasks.await

/**
 * Automatic Empty-Database Cloud Firestore Initializer.
 *
 * Checks if the Cloud Firestore 'songs' collection is empty on startup.
 * If empty (`snapshot.isEmpty`), automatically seeds all 5 Firestore collections:
 *  1. 'users': SuperAdmin user profile (`users/SUPERADMIN_FERWINRED`).
 *  2. 'songs': Catalog worship songs.
 *  3. 'chords': 120 Guitar & Piano chord diagrams.
 *  4. 'promo_codes': Subscription tier promo codes.
 *  5. 'setlists': Sample worship setlists.
 * using direct document `.set(...)` calls in Cloud Firestore.
 *
 * If NOT empty (`snapshot.isNotEmpty`), skips seeding completely to guarantee **ZERO DUPLICATION**.
 */
object AutomaticFirestoreInitializer {

    const val COLLECTION_SONGS = "songs"
    const val COLLECTION_USERS = "users"
    const val COLLECTION_CHORDS = "chords"
    const val COLLECTION_PROMO_CODES = "promo_codes"
    const val COLLECTION_SETLISTS = "setlists"

    private var forceEmptyForTesting: Boolean? = null
    private var isSeededInMemory = false

    private fun safeLogWarning(message: String?) {
        try {
            Log.w("AutomaticFirestore", "Firestore initialization notice: $message")
        } catch (_: Throwable) {
            println("AutomaticFirestore: Firestore initialization notice: $message")
        }
    }

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
        } catch (e: Throwable) {
            safeLogWarning(e.message)
            null
        }
    }

    /**
     * Checks if Cloud Firestore 'songs' collection is empty.
     * If empty (`snapshot.isEmpty`), automatically seeds all 5 collections using direct document `.set(...)` calls.
     * If NOT empty (`snapshot.isNotEmpty`), skips seeding completely to guarantee ZERO DUPLICATION.
     *
     * On exception or failure:
     *  - Logs diagnostic warning: `Log.w("AutomaticFirestore", "Firestore initialization notice: ${e.message}")`.
     *  - Executes `ProductionFirestoreInitializer.seedLocalStores(context)` to guarantee local persistent storage (`songs_db.json`, `setlists_db.json`) is 100% seeded.
     *  - Completes callback gracefully (`onComplete?.invoke(false)`).
     *
     * @return `true` if database was empty and seeding was executed; `false` if skipped or failed.
     */
    suspend fun checkAndSeedIfEmpty(
        context: Context? = null,
        firestore: FirebaseFirestore? = getFirestore(),
        isDbEmptyOverride: Boolean? = null,
        onComplete: ((Boolean) -> Unit)? = null,
    ): Boolean {
        return try {
            val isDbEmpty = when {
                isDbEmptyOverride != null -> isDbEmptyOverride
                forceEmptyForTesting != null -> forceEmptyForTesting!!
                firestore != null -> {
                    try {
                        val task = firestore.collection(COLLECTION_SONGS).limit(1).get()
                        task.addOnFailureListener { e ->
                            safeLogWarning(e.message)
                        }
                        val snapshot = task.await()
                        snapshot.isEmpty
                    } catch (e: FirebaseFirestoreException) {
                        safeLogWarning(e.message)
                        ProductionFirestoreInitializer.seedLocalStores(context)
                        onComplete?.invoke(false)
                        return false
                    } catch (e: Throwable) {
                        safeLogWarning(e.message)
                        ProductionFirestoreInitializer.seedLocalStores(context)
                        onComplete?.invoke(false)
                        return false
                    }
                }
                else -> {
                    // JVM Unit testing / offline fallback when firestore is null
                    !isSeededInMemory && ProductionFirestoreInitializer.getSongs().isEmpty()
                }
            }

            if (isDbEmpty) {
                seedAllExplicitly(context, firestore, onComplete)
            } else {
                // Snapshot NOT empty -> SKIP seeding completely to guarantee ZERO DUPLICATION!
                onComplete?.invoke(false)
                false
            }
        } catch (e: FirebaseFirestoreException) {
            safeLogWarning(e.message)
            ProductionFirestoreInitializer.seedLocalStores(context)
            onComplete?.invoke(false)
            false
        } catch (e: Throwable) {
            safeLogWarning(e.message)
            ProductionFirestoreInitializer.seedLocalStores(context)
            onComplete?.invoke(false)
            false
        }
    }

    /**
     * Seeds all 5 collections explicitly in Cloud Firestore and populates local storage.
     * Catches exceptions on document `.set(...)` calls so individual document write failures do not interrupt execution.
     */
    suspend fun seedAllExplicitly(
        context: Context? = null,
        firestore: FirebaseFirestore? = getFirestore(),
        onComplete: ((Boolean) -> Unit)? = null,
    ): Boolean {
        return try {
            ProductionFirestoreInitializer.initializeAll(
                context = context,
                firestore = firestore,
            )
            isSeededInMemory = true
            onComplete?.invoke(true)
            true
        } catch (e: FirebaseFirestoreException) {
            safeLogWarning(e.message)
            ProductionFirestoreInitializer.seedLocalStores(context)
            onComplete?.invoke(false)
            false
        } catch (e: Throwable) {
            safeLogWarning(e.message)
            ProductionFirestoreInitializer.seedLocalStores(context)
            onComplete?.invoke(false)
            false
        }
    }

    fun setForceEmptyForTesting(empty: Boolean?) {
        forceEmptyForTesting = empty
        if (empty == true) {
            isSeededInMemory = false
        }
    }

    fun isSeededInMemory(): Boolean = isSeededInMemory

    fun resetForTesting() {
        forceEmptyForTesting = null
        isSeededInMemory = false
        ProductionFirestoreInitializer.clearInMemoryStore()
    }
}
