package com.example.songchords.subscription

import com.example.songchords.R

/**
 * 5-Tier Membership model defining plan benefits and feature limits.
 */
enum class UserTier(
    val titleResId: Int,
    val priceResId: Int,
    val maxSetlists: Int,
    val maxSongsPerSetlist: Int,
    val maxCustomSongs: Int,
    val isAdFree: Boolean,
    val hasAiAssistant: Boolean
) {
    ANONYMOUS(
        titleResId = R.string.tier_anonymous,
        priceResId = R.string.price_free,
        maxSetlists = 0,
        maxSongsPerSetlist = 0,
        maxCustomSongs = 0,
        isAdFree = false,
        hasAiAssistant = false
    ),
    REGISTERED_FREE(
        titleResId = R.string.tier_registered_free,
        priceResId = R.string.price_free,
        maxSetlists = 3,
        maxSongsPerSetlist = 15,
        maxCustomSongs = 2,
        isAdFree = false,
        hasAiAssistant = false
    ),
    BASIC_SUB(
        titleResId = R.string.tier_basic_sub,
        priceResId = R.string.price_basic,
        maxSetlists = 7,
        maxSongsPerSetlist = 20,
        maxCustomSongs = 5,
        isAdFree = true,
        hasAiAssistant = false
    ),
    PRO_SUB(
        titleResId = R.string.tier_pro_sub,
        priceResId = R.string.price_pro,
        maxSetlists = Int.MAX_VALUE,
        maxSongsPerSetlist = 20,
        maxCustomSongs = 30,
        isAdFree = true,
        hasAiAssistant = false
    ),
    PREMIUM_AI_SUB(
        titleResId = R.string.tier_premium_ai_sub,
        priceResId = R.string.price_premium_ai,
        maxSetlists = Int.MAX_VALUE,
        maxSongsPerSetlist = Int.MAX_VALUE,
        maxCustomSongs = Int.MAX_VALUE,
        isAdFree = true,
        hasAiAssistant = true
    )
}
