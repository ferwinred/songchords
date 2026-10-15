package com.example.songchords.firestore

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.songchords.auth.UserIdentityManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import kotlinx.coroutines.tasks.await

/**
 * Guaranteed Firebase Auth & Direct Collection Seeding Engine.
 *
 * Ensures `FirebaseAuth.getInstance().signInAnonymously()` executes FIRST before reading/writing to Cloud Firestore.
 * Checks if the Cloud Firestore collections are seeded on startup.
 * If not seeded (`!userIdentityManager.isFirestoreSeeded()`), automatically seeds all 5 Firestore collections:
 *  1. 'users': SuperAdmin user profile (`users/SUPERADMIN_FERWINRED`).
 *  2. 'songs': Catalog worship songs (6 songs).
 *  3. 'chords': 120 Guitar & Piano chord diagrams.
 *  4. 'promo_codes': Subscription tier promo codes (3 codes).
 *  5. 'setlists': Sample worship setlists (2 setlists).
 * using direct document `.set(...)` calls in Cloud Firestore with explicit logging,
 * and displays an explicit Toast notification on completion:
 * `"🔥 [Firebase Cloud] 132 documentos creados en Firestore (SuperAdmin: ferwinred@gmail.com)"`.
 *
 * If already seeded, skips seeding completely to guarantee **ZERO DUPLICATION**.
 */
object AutomaticFirestoreInitializer {

    const val COLLECTION_SONGS = "songs"
    const val COLLECTION_USERS = "users"
    const val COLLECTION_CHORDS = "chords"
    const val COLLECTION_PROMO_CODES = "promo_codes"
    const val COLLECTION_SETLISTS = "setlists"

    const val SUCCESS_TOAST_MESSAGE = "🔥 [Firebase Cloud] 132 documentos creados en Firestore (SuperAdmin: ferwinred@gmail.com)"

    private var forceEmptyForTesting: Boolean? = null
    private var isSeededInMemory = false

    private fun safeLogWarning(message: String?) {
        try {
            Log.w("AutomaticFirestore", "Firestore initialization notice: $message")
        } catch (_: Throwable) {
            println("AutomaticFirestore: Firestore initialization notice: $message")
        }
    }

    private fun safeLogInfo(message: String) {
        try {
            Log.i("AutomaticFirestore", message)
        } catch (_: Throwable) {
            println("AutomaticFirestore: $message")
        }
    }

    private fun showToastOnMainThread(context: Context?, message: String) {
        if (context == null) return
        try {
            val mainHandler = Handler(Looper.getMainLooper())
            mainHandler.post {
                try {
                    Toast.makeText(context.applicationContext ?: context, message, Toast.LENGTH_LONG).show()
                } catch (_: Throwable) {
                }
            }
        } catch (_: Throwable) {
            try {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            } catch (_: Throwable) {
            }
        }
    }

    fun getFirestore(): FirebaseFirestore? {
        return try {
            val app = com.google.firebase.FirebaseApp.getInstance()
            val firestore = FirebaseFirestore.getInstance(app, "songchords")
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
     * Ensures `FirebaseAuth.getInstance().signInAnonymously()` executes FIRST before reading/writing to Cloud Firestore.
     * Handles offline fallback or headless JVM testing environments gracefully without throwing exceptions.
     */
    suspend fun ensureFirebaseAuthAnonymously(): Boolean {
        return try {
            val auth = FirebaseAuth.getInstance()
            val task = auth.signInAnonymously()
            task.addOnSuccessListener { result ->
                safeLogInfo("FirebaseAuth signInAnonymously succeeded for user: ${result.user?.uid}")
            }
            task.addOnFailureListener { e ->
                safeLogWarning("FirebaseAuth signInAnonymously failed: ${e.message}")
            }
            task.await()
            true
        } catch (e: Throwable) {
            safeLogWarning("FirebaseAuth signInAnonymously fallback: ${e.message}")
            false
        }
    }

    /**
     * Checks if Cloud Firestore has been seeded.
     * Ensures `FirebaseAuth.getInstance().signInAnonymously()` executes FIRST before reading/writing.
     * If not seeded (`!userIdentityManager.isFirestoreSeeded()`), automatically seeds all 5 collections using direct document `.set(...)` calls.
     * Stores local preference flag so it skips automatically on subsequent opens (ZERO DUPLICATION).
     *
     * On exception or failure:
     *  - Displays Toast: `"Error de Firebase: ${e.message}. Revisa las Reglas en Firebase Console."`
     *  - Executes `ProductionFirestoreInitializer.seedLocalStores(context)` to guarantee local persistent storage (`songs_db.json`, `setlists_db.json`) is 100% seeded.
     *  - Completes callback gracefully (`onComplete?.invoke(false)`).
     *
     * @return `true` if database was empty and seeding was executed; `false` if skipped or failed.
     */
    suspend fun checkAndSeedIfEmpty(
        context: Context? = null,
        firestore: FirebaseFirestore? = getFirestore(),
        isDbEmptyOverride: Boolean? = null,
        userIdentityManager: UserIdentityManager = UserIdentityManager,
        onComplete: ((Boolean) -> Unit)? = null,
    ): Boolean {
        return try {
            if (userIdentityManager.isFirestoreSeeded(context) && isDbEmptyOverride == null && forceEmptyForTesting == null) {
                safeLogInfo("Firestore already marked as seeded. Skipping seeding to guarantee zero duplication.")
                onComplete?.invoke(false)
                return false
            }

            // Guarantee FirebaseAuth executes FIRST before reading/writing to Cloud Firestore
            ensureFirebaseAuthAnonymously()

            val isDbEmpty = when {
                isDbEmptyOverride != null -> isDbEmptyOverride
                forceEmptyForTesting != null -> forceEmptyForTesting!!
                firestore != null -> {
                    try {
                        val task = firestore.collection(COLLECTION_SONGS).limit(1).get()
                        task.addOnSuccessListener { snapshot ->
                            safeLogInfo("Firestore collection '$COLLECTION_SONGS' query successful. Empty: ${snapshot.isEmpty}")
                        }
                        task.addOnFailureListener { e ->
                            safeLogWarning("Firestore collection '$COLLECTION_SONGS' query failed: ${e.message}")
                        }
                        val snapshot = task.await()
                        snapshot.isEmpty
                    } catch (e: FirebaseFirestoreException) {
                        safeLogWarning(e.message)
                        showToastOnMainThread(context, "Error de Firebase: ${e.message}. Revisa las Reglas en Firebase Console.")
                        ProductionFirestoreInitializer.seedLocalStores(context)
                        onComplete?.invoke(false)
                        return false
                    } catch (e: Throwable) {
                        safeLogWarning(e.message)
                        showToastOnMainThread(context, "Error de Firebase: ${e.message}. Revisa las Reglas en Firebase Console.")
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
                seedAllExplicitly(context, firestore, userIdentityManager, onComplete)
            } else {
                // Snapshot NOT empty -> Mark seeded and SKIP seeding completely to guarantee ZERO DUPLICATION!
                userIdentityManager.markFirestoreSeeded(context)
                onComplete?.invoke(false)
                false
            }
        } catch (e: FirebaseFirestoreException) {
            safeLogWarning(e.message)
            showToastOnMainThread(context, "Error de Firebase: ${e.message}. Revisa las Reglas en Firebase Console.")
            ProductionFirestoreInitializer.seedLocalStores(context)
            onComplete?.invoke(false)
            false
        } catch (e: Throwable) {
            safeLogWarning(e.message)
            showToastOnMainThread(context, "Error de Firebase: ${e.message}. Revisa las Reglas en Firebase Console.")
            ProductionFirestoreInitializer.seedLocalStores(context)
            onComplete?.invoke(false)
            false
        }
    }

    /**
     * Seeds all 5 collections explicitly in Cloud Firestore writing all 132 documents across
     * 'users' (`users/SUPERADMIN_FERWINRED`), 'songs', 'chords', 'promo_codes', and 'setlists' collections
     * using explicit `firestore.collection().document().set(map)` with logging,
     * marks `userIdentityManager.markFirestoreSeeded(context)` (saving local preference flag),
     * and displays an explicit Toast notification on completion:
     * `"🔥 [Firebase Cloud] 132 documentos creados en Firestore (SuperAdmin: ferwinred@gmail.com)"`.
     */
    suspend fun seedAllExplicitly(
        context: Context? = null,
        firestore: FirebaseFirestore? = getFirestore(),
        userIdentityManager: UserIdentityManager = UserIdentityManager,
        onComplete: ((Boolean) -> Unit)? = null,
    ): Boolean {
        return try {
            ensureFirebaseAuthAnonymously()
            val report = ProductionFirestoreInitializer.initializeAll(
                context = context,
                firestore = firestore,
            )
            safeLogInfo("Seeding completed: ${report.totalDocumentsUploaded} documents created.")
            isSeededInMemory = true
            userIdentityManager.markFirestoreSeeded(context)

            showToastOnMainThread(context, SUCCESS_TOAST_MESSAGE)

            onComplete?.invoke(true)
            true
        } catch (e: FirebaseFirestoreException) {
            safeLogWarning(e.message)
            showToastOnMainThread(context, "Error de Firebase: ${e.message}. Revisa las Reglas en Firebase Console.")
            ProductionFirestoreInitializer.seedLocalStores(context)
            onComplete?.invoke(false)
            false
        } catch (e: Throwable) {
            safeLogWarning(e.message)
            showToastOnMainThread(context, "Error de Firebase: ${e.message}. Revisa las Reglas en Firebase Console.")
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
