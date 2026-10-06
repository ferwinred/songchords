# Project Plan

SongChords app - Prepare Production Beta Release with SuperAdmin catalog, Firestore chord library, Firebase Auth (Google & Anonymous), Google Play Billing, AdMob Ads, and One-Time Promo Code Redemption system.

## Project Brief

# Project Brief: SongChords App (Beta Release)

## Features

1. **Firebase Authentication (Google Sign-In & Anonymous)**: Seamless user onboarding supporting Google Sign-In and Anonymous guest mode.
2. **Firestore Song Catalog & Chord Library**: Cloud database storing Guitar and Piano chord dictionaries (`chords` collection) and catalog songs associated with SuperAdmin management (`ferwinred@gmail.com`).
3. **Subscriptions & One-Time Promo Code System**: Tiered subscription management via Google Play Billing ($2.99, $5.99, $9.99/mo) along with a promo code redemption mechanism (`promo_codes` collection) to unlock premium access.
4. **AdMob Integration for Free Tier**: Monetization via Google Mobile Ads SDK for non-paying users, automatically hidden for active subscribers and redeemed promo accounts.

## High-Level Technical Stack

- **Language & Concurrency**: Kotlin, Kotlin Coroutines, Kotlin Flow
- **UI Framework & Adaptive Layouts**: Jetpack Compose, Material Design 3, Compose Material Adaptive library
- **Navigation**: Jetpack Navigation 3 (state-driven navigation)
- **Backend & Cloud Data**: Firebase Authentication, Cloud Firestore
- **Monetization & Ads**: Google Play Billing Library, Google Mobile Ads SDK (AdMob)

## Implementation Steps
**Total Duration:** 27m 46s

### Task_93_ImplementSuperAdminCatalogAndFirestoreChordLibrary: Implement SuperAdmin Catalog & Firestore Chord Library: 1) Update SampleSongs.kt to associate catalog songs with SuperAdmin (email: ferwinred@gmail.com, name: 'SuperAdmin'). 2) Create FirestoreChordLibraryRepository to persist and sync Guitar & Piano chord dictionaries to Firestore 'chords' collection. 3) Commit and push updates to GitHub.
- **Status:** COMPLETED
- **Updates:** Task_93_ImplementSuperAdminCatalogAndFirestoreChordLibrary completed successfully. Implemented SuperAdmin Catalog & Firestore Chord Library: 1) Associated all sample catalog songs in SampleSongs.kt with SuperAdmin account (SUPER_ADMIN_EMAIL = 'ferwinred@gmail.com', SUPER_ADMIN_ID = 'SUPERADMIN_FERWINRED'). 2) Created FirestoreChordLibraryRepository in com.example.songchords.repository to persist and sync Guitar & Piano chord diagrams to Firestore 'chords' collection with offline cache fallback. Verified with assembleDebug build and 97 passing unit tests.
- **Acceptance Criteria:**
  - SampleSongs associated with SuperAdmin (ferwinred@gmail.com)
  - FirestoreChordLibraryRepository created for Guitar & Piano chord dictionary persistence in Firestore
  - build pass

### Task_94_ImplementFirebaseAuthBillingAdMobAndPromoCodes: Implement Beta Launch Suite (Firebase Auth, Play Billing, AdMob, One-Time Promo Codes): 1) Integrate FirebaseAuthManager for Google Sign-In and Anonymous Authentication. 2) Implement PlayBillingManager for Google Play In-App Subscriptions. 3) Implement AdMobManager for AdMob banner ads on free tiers. 4) Create PromoCodeManager and PromoCodeRedemptionDialog in SubscriptionScreen to validate single-use promo codes (e.g. 'BETA-PRO-2026', 'VIP-PRAYER-99') in Firestore 'promo_codes' collection and grant subscriptions without payment.
- **Status:** COMPLETED
- **Updates:** Task_94_ImplementFirebaseAuthBillingAdMobAndPromoCodes completed successfully. Implemented Beta Launch Suite: 1) Created FirebaseAuthManager for Google Sign-In and Anonymous login. 2) Created PlayBillingManager for Google Play In-App Subscriptions ($2.99, $5.99, $9.99). 3) Created AdMobManager and updated AdBannerComposable for AdMob banner ads on free tiers. 4) Created PromoCodeManager and PromoCodeRedemptionDialog in SubscriptionScreen.kt for single-use code redemption (BETA-PRO-2026, VIP-PRAYER-99, BASIC-FREE-2026) backed by Firestore 'promo_codes' collection to grant subscriptions without payment. 5) Added PromoCodeManagerTest, FirebaseAuthManagerTest, and PlayBillingManagerTest unit test suites and localized EN/ES strings. Verified with assembleDebug build and 111 passing unit tests.
- **Acceptance Criteria:**
  - FirebaseAuthManager implemented for Google and Anonymous login
  - PlayBillingManager implemented for Play Store subscriptions
  - AdMobManager implemented for banner ads
  - PromoCodeManager implemented for one-time code redemption in SubscriptionScreen
  - build pass
- **Duration:** 11m 19s

### Task_95_RunAndVerifyBetaLaunchSuite: Verify SuperAdmin catalog, Firestore Chord Library, Firebase Auth, Play Billing, AdMob, and Promo Code Redemption on emulator with critic_agent. Ensure zero crashes.
- **Status:** COMPLETED
- **Updates:** Task_95_RunAndVerifyBetaLaunchSuite completed successfully. Verified on emulator with 0 crashes. Confirmed all Beta Launch Suite features: 1) SuperAdmin catalog songs associated with ferwinred@gmail.com. 2) Firestore Chord Library for Guitar & Piano chord diagrams. 3) Firebase Auth (Google & Anonymous sign-in). 4) Google Play Billing manager structure. 5) AdMob banner ads on free tiers, automatically hidden on paid tiers. 6) One-time Promo Code redemption system (VIP-PRAYER-99, BETA-PRO-2026, BASIC-FREE-2026) in SubscriptionScreen granting subscriptions without payment. All 111 unit tests pass.
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - Beta launch suite verified clean and functional on emulator
- **Duration:** 16m 27s

