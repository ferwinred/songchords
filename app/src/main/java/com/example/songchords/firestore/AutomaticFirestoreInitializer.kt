package com.example.songchords.firestore

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
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
     * Checks if Cloud Firestore 'songs' collection is empty.
     * If empty (`snapshot.isEmpty`), automatically seeds all 5 collections using direct document `.set(...)` calls.
     * If NOT empty (`snapshot.isNotEmpty`), skips seeding completely to guarantee ZERO DUPLICATION.
     *
     * @return `true` if database was empty and seeding was executed; `false` if skipped.
     */
    suspend fun checkAndSeedIfEmpty(
        context: Context? = null,
        firestore: FirebaseFirestore? = getFirestore(),
        isDbEmptyOverride: Boolean? = null,
    ): Boolean {
        val isDbEmpty = when {
            isDbEmptyOverride != null -> isDbEmptyOverride
            forceEmptyForTesting != null -> forceEmptyForTesting!!
            firestore != null -> {
                try {
                    val snapshot = firestore.collection(COLLECTION_SONGS).limit(1).get().await()
                    snapshot.isEmpty
                } catch (_: Throwable) {
                    // Fallback if network or uninitialized
                    false
                }
            }
            else -> {
                // JVM Unit testing / offline fallback when firestore is null
                !isSeededInMemory && ProductionFirestoreInitializer.getSongs().isEmpty()
            }
        }

        if (isDbEmpty) {
            // Automatically seed all 5 collections using direct document .set(...) calls in Cloud Firestore
            ProductionFirestoreInitializer.initializeAll(
                context = context,
                firestore = firestore,
            )
            isSeededInMemory = true
            return true
        } else {
            // Snapshot NOT empty -> SKIP seeding completely to guarantee ZERO DUPLICATION!
            return false
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
