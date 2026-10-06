# Project Plan

SongChords app - Implement automatic first-launch one-time Firestore database seeding using app/google-services.json and remove manual seeding buttons from UI.

## Project Brief

# Project Brief: SongChords

## Features
- **Automatic First-Launch Seeding**: Automatically executes `FirestoreSeeder.seedAllCollections()` on the app's initial launch.
- **One-Time Execution Tracking**: Persists a boolean flag (`is_firestore_seeded`) in preferences to ensure database seeding runs only once.
- **Real Cloud Firestore Integration**: Connects directly to Cloud Firestore using the `app/google-services.json` configuration.
- **Cleaned User Interface**: Removes redundant manual seeding buttons from `ToolsScreen` and `SubscriptionScreen` for a clutter-free experience.

## High-Level Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Navigation & Adaptive Strategy**: Jetpack Navigation 3 (state-driven) and Compose Material Adaptive library
- **Asynchronous Programming**: Kotlin Coroutines & Flow
- **Cloud Database**: Firebase Cloud Firestore
- **Local Preferences**: Jetpack DataStore / SharedPreferences

## Implementation Steps
**Total Duration:** 2m 3s

### Task_98_ImplementFirstLaunchOneTimeSeedingAndRemoveButton: Implement Automatic First-Launch One-Time Firestore Seeding & Remove Seeding Button: 1) In CloudSyncEngine.kt / MainActivity.kt, check boolean preference 'is_firestore_seeded'. If false, run FirestoreSeeder.seedAllCollections() on first launch and set preference to true so it never runs again. 2) Remove the '🔥 Inicializar / Sembrar Colecciones de Firestore' button from ToolsScreen.kt and SubscriptionScreen.kt. 3) Commit and push updates to GitHub.
- **Status:** COMPLETED
- **Updates:** Task_98_ImplementFirstLaunchOneTimeSeedingAndRemoveButton completed successfully. Implemented automatic one-time first-launch Firestore seeding and removed manual seed button: 1) Added isFirestoreSeeded() and markFirestoreSeeded() preference methods to UserIdentityManager.kt. 2) Updated CloudSyncEngine.kt to trigger FirestoreSeeder.seedAllCollections(FirebaseFirestore.getInstance()) automatically ONCE on first launch using google-services.json, marking preference complete upon finish so it never runs again. 3) Removed manual '🔥 Inicializar / Sembrar Colecciones' button from ToolsScreen.kt and SubscriptionScreen.kt. Verified with assembleDebug build and 118 passing unit tests.
- **Acceptance Criteria:**
  - FirestoreSeeder runs automatically ONCE on first launch using google-services.json
  - Preference 'is_firestore_seeded' prevents re-seeding on subsequent launches
  - Manual seeding button removed from ToolsScreen and SubscriptionScreen
  - build pass

### Task_99_RunAndVerifyFirstLaunchSeeding: Verify first-launch one-time seeding and clean UI on emulator with critic_agent. Ensure zero crashes.
- **Status:** COMPLETED
- **Updates:** Task_99_RunAndVerifyFirstLaunchSeeding completed successfully. Verified on emulator with 0 crashes. Confirmed: 1) Manual seed button completely removed from ToolsScreen and SubscriptionScreen. 2) Automatic one-time Firestore seeding runs in background on first launch using google-services.json and sets isFirestoreSeeded = true so it never runs again on subsequent launches. All 118 unit tests pass cleanly.
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - First-launch one-time seeding and clean UI verified on emulator
- **Duration:** 2m 3s

