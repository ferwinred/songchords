package com.example.songchords.subscription

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Data class representing a single-use promo code granting a specific membership tier.
 */
data class PromoCode(
    val code: String,
    val targetTier: UserTier,
    val isRedeemed: Boolean = false,
    val redeemedByUserId: String? = null,
    val redeemedAtTimestamp: Long? = null,
)

/**
 * Manages single-use promo code validation and redemption stored in Firestore 'promo_codes'.
 */
@Suppress("unused")
object PromoCodeManager {

    const val FIRESTORE_COLLECTION_PROMO_CODES = "promo_codes"

    private val INITIAL_PROMO_CODES = mapOf(
        "BETA-PRO-2026" to PromoCode("BETA-PRO-2026", UserTier.PRO_SUB),
        "VIP-PRAYER-99" to PromoCode("VIP-PRAYER-99", UserTier.PREMIUM_AI_SUB),
        "BASIC-FREE-2026" to PromoCode("BASIC-FREE-2026", UserTier.BASIC_SUB)
    )

    private val localStore = mutableMapOf<String, PromoCode>().apply {
        putAll(INITIAL_PROMO_CODES)
    }

    private fun getFirestore(): FirebaseFirestore? {
        return try {
            val firestore = FirebaseFirestore.getInstance()
            try {
                val settings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
                    .setLocalCacheSettings(com.google.firebase.firestore.PersistentCacheSettings.newBuilder().build())
                    .build()
                firestore.firestoreSettings = settings
            } catch (_: Throwable) {
                // Ignore if settings were already set or instance initialized
            }
            firestore
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Validates and redeems a single-use promo code for the specified user ID.
     * Updates Firestore 'promo_codes' collection and updates active SubscriptionManager user tier.
     */
    suspend fun redeemPromoCode(code: String, userId: String): Result<UserTier> {
        val normalizedCode = code.trim().uppercase()

        if (normalizedCode.isEmpty()) {
            return Result.failure(IllegalArgumentException("El código no puede estar vacío"))
        }

        var promoCode = localStore[normalizedCode]

        if (promoCode == null) {
            // Attempt to fetch from Firestore remote collection if available
            getFirestore()?.let { db ->
                try {
                    val snapshot = db.collection(FIRESTORE_COLLECTION_PROMO_CODES)
                        .document(normalizedCode)
                        .get()
                        .await()

                    if (snapshot.exists()) {
                        val tierName = snapshot.getString("targetTier") ?: ""
                        val tier = runCatching { UserTier.valueOf(tierName) }.getOrDefault(UserTier.REGISTERED_FREE)
                        val isRedeemed = snapshot.getBoolean("isRedeemed") ?: false
                        val redeemedBy = snapshot.getString("redeemedByUserId")
                        val redeemedAt = snapshot.getLong("redeemedAtTimestamp")

                        val fetchedCode = PromoCode(
                            code = normalizedCode,
                            targetTier = tier,
                            isRedeemed = isRedeemed,
                            redeemedByUserId = redeemedBy,
                            redeemedAtTimestamp = redeemedAt
                        )
                        localStore[normalizedCode] = fetchedCode
                        promoCode = fetchedCode
                    }
                } catch (_: Exception) {
                    // Fall back to local store on network/firestore error
                }
            }
        }

        val targetPromo = promoCode
            ?: return Result.failure(IllegalArgumentException("Código promocional no válido"))

        if (targetPromo.isRedeemed) {
            return Result.failure(IllegalStateException("Este código promocional ya ha sido canjeado"))
        }

        val timestamp = System.currentTimeMillis()
        val redeemedPromo = targetPromo.copy(
            isRedeemed = true,
            redeemedByUserId = userId,
            redeemedAtTimestamp = timestamp
        )

        // Mark as used in local store
        localStore[normalizedCode] = redeemedPromo

        // Sync redemption state to Firestore
        getFirestore()?.let { db ->
            try {
                val data = mapOf(
                    "code" to redeemedPromo.code,
                    "targetTier" to redeemedPromo.targetTier.name,
                    "isRedeemed" to true,
                    "redeemedByUserId" to userId,
                    "redeemedAtTimestamp" to timestamp
                )
                db.collection(FIRESTORE_COLLECTION_PROMO_CODES)
                    .document(normalizedCode)
                    .set(data)
                    .await()
            } catch (_: Exception) {
                // Ignore offline sync errors
            }
        }

        // Update SubscriptionManager active tier without payment
        SubscriptionManager.setTier(redeemedPromo.targetTier)

        return Result.success(redeemedPromo.targetTier)
    }

    fun getPromoCode(code: String): PromoCode? {
        return localStore[code.trim().uppercase()]
    }

    fun addPromoCode(promoCode: PromoCode) {
        localStore[promoCode.code.trim().uppercase()] = promoCode
    }

    fun resetToDefault() {
        localStore.clear()
        localStore.putAll(INITIAL_PROMO_CODES)
    }
}
