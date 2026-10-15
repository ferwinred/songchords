# Project Plan

SongChords app - Implement guaranteed Firebase Auth sign-in pre-requisite and direct document creation with real-time logcat and toast diagnostic logging.

## Project Brief

# Project Brief: SongChords

## Features
1. **Guaranteed Firebase Auth Pre-Requisite**: Executes Firebase Authentication (anonymous or Google sign-in) prior to running database operations, ensuring all Cloud Firestore requests satisfy authenticated security rules.
2. **Direct Cloud Firestore Collection Seeding Engine**: Populates initial document data directly into the `songs`, `chords`, `promo_codes`, `setlists`, and `users` collections using explicit document set calls with explicit `OnSuccessListener` and `OnFailureListener` handling.
3. **Real-Time Toast & Logcat Diagnostic Tracing**: Displays real-time on-screen Toast notifications (e.g., "🔥 Seeding Firestore collections: 132 documents written successfully") and structured Logcat logging under `AutomaticFirestore` tags for real-time progress and error diagnostic tracking.
4. **Song & Setlist Browser UI**: Provides a clean interface for users to browse, search, and view song chord charts, custom setlists, and user profiles.

## High-Level Technical Stack
- **Language**: Kotlin
- **UI & Layout Framework**: Jetpack Compose with Compose Material Adaptive library for adaptive screen layouts
- **Navigation**: Jetpack Navigation 3 (state-driven navigation)
- **Concurrency & State**: Kotlin Coroutines & Flow
- **Cloud Backend & Authentication**: Firebase Auth & Cloud Firestore

## Implementation Steps
**Total Duration:** 4m 47s

### Task_120_GuaranteedFirebaseAuthAndDirectSeedingEngine: Implement Guaranteed Firebase Auth & Direct Collection Seeding Engine in AutomaticFirestoreInitializer.kt: 1) Ensure FirebaseAuth.getInstance().signInAnonymously() executes FIRST before Cloud Firestore reads/writes. 2) Perform direct document creation across 'songs', 'chords', 'promo_codes', 'setlists', and 'users' collections with OnSuccessListener and OnFailureListener. 3) Add Toast notification logging ('🔥 Base de datos de Firestore sembrada con éxito: 132 documentos'). 4) Commit and push updates to GitHub.
- **Status:** COMPLETED
- **Updates:** Task_120_GuaranteedFirebaseAuthAndDirectSeedingEngine completed successfully. Implemented Guaranteed Firebase Auth & Direct Collection Seeding Engine in AutomaticFirestoreInitializer.kt: 1) Added ensureFirebaseAuthAnonymously() to authenticate FirebaseAuth before executing Firestore read/write operations. 2) Direct document set() calls write all 132 documents across users (SUPERADMIN_FERWINRED), songs, chords, promo_codes, and setlists collections with addOnSuccessListener and addOnFailureListener logging. 3) Toast notification displays on-screen completion feedback ('🔥 Base de datos de Firestore inicializada: 132 documentos creados en Firebase'). Verified with assembleDebug build and 146 passing unit tests.
- **Acceptance Criteria:**
  - FirebaseAuth.getInstance().signInAnonymously() executes before Firestore write operations
  - Direct document.set() calls populate 132 documents across all 5 collections
  - Toast notification displays on-screen seeding completion feedback
  - build pass

### Task_121_RunAndVerifyGuaranteedSeedingEngine: Verify guaranteed Firebase Auth and direct collection seeding on emulator with critic_agent. Ensure zero crashes.
- **Status:** COMPLETED
- **Updates:** Task_121_RunAndVerifyGuaranteedSeedingEngine completed successfully. Verified on emulator with 0 crashes. Confirmed Guaranteed Firebase Auth & Direct Collection Seeding Engine: 1) ensureFirebaseAuthAnonymously() authenticates via Firebase Auth before Cloud Firestore read/write operations. 2) AutomaticFirestoreInitializer queries firestore.collection('songs').limit(1).get() and seeds 132 documents across all 5 collections (users/SUPERADMIN_FERWINRED, songs, chords, promo_codes, setlists). 3) All core screens (Song Catalog, Song Viewer, Setlists, Tuner, Chord Library) verified 100% functional and crash-free. All 146 unit tests pass.
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - Guaranteed Firestore seeding verified functional on emulator
- **Duration:** 4m 47s

