package com.example.songchords.ads

import android.content.Context
import com.example.songchords.subscription.UserTier
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

/**
 * Manages AdMob initialization and AdView creation for free-tier users.
 */
@Suppress("unused")
object AdMobManager {

    const val TEST_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"

    private var isInitialized = false

    fun initialize(context: Context) {
        if (!isInitialized) {
            try {
                MobileAds.initialize(context)
                isInitialized = true
            } catch (_: Exception) {
                // Ignore initialization errors in preview or headless environment
            }
        }
    }

    /**
     * Determines whether ad banners should be displayed for a given membership tier.
     * Shows ads only for ANONYMOUS and REGISTERED_FREE tiers.
     */
    fun shouldShowAd(tier: UserTier): Boolean {
        return (tier == UserTier.ANONYMOUS) || (tier == UserTier.REGISTERED_FREE)
    }

    /**
     * Creates and loads a banner AdView using Google test ad unit ID.
     */
    fun createAdView(context: Context, adUnitId: String = TEST_AD_UNIT_ID): AdView {
        val adView = AdView(context)
        adView.setAdSize(AdSize.BANNER)
        adView.adUnitId = adUnitId
        try {
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        } catch (_: Exception) {
            // Ignore load failures during offline or test mode
        }
        return adView
    }
}
