# Project Plan

SongChords app - Build automated Firebase Firestore Collection Seeding Engine (FirestoreSeeder.kt) to populate songs, chords, promo_codes, and setlists collections in Cloud Firestore.

## Project Brief

# Project Brief: SongChords

## Features

1. **Automated Firestore Seeding Engine (`FirestoreSeeder.kt`)**  
   Executes automated seeding into Cloud Firestore across four core collections:
   - **`songs`**: SuperAdmin catalog songs tagged with `'SUPERADMIN_FERWINRED'` ownership.
   - **`chords`**: Guitar and Piano chord diagrams covering 12 root notes and 10 chord qualities.
   - **`promo_codes`**: Single-use promotional tier codes (`VIP-PRAYER-99`, `BETA-PRO-2026`, `BASIC-FREE-2026`).
   - **`setlists`**: Sample worship setlists for initial app content.

2. **Firebase Cloud Connection Helper**  
   Initializes and verifies Cloud Firestore connectivity via `FirebaseFirestore.getInstance()`, ensuring database collections are automatically initialized on launch.

3. **Seeding Control & Status Dashboard**  
   A Jetpack Compose administration UI view allowing manual or automated triggering of database seeding, real-time progress monitoring, and seed verification.

4. **Adaptive Song & Chord Catalog Browser**  
   An adaptive, state-driven UI layout allowing users to inspect seeded songs, chord diagrams, promo codes, and setlists across diverse Android device form factors.

---

## High-Level Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Navigation & Adaptive Strategy**: Jetpack Navigation 3 (state-driven) & Compose Material Adaptive library
- **Asynchronous & Concurrency**: Kotlin Coroutines & StateFlow
- **Backend / Cloud Database**: Firebase Cloud Firestore SDK
- **Architecture**: MVVM (Model-View-ViewModel) Architecture

## Implementation Steps
**Total Duration:** 2m 22s

### Task_96_ImplementFirestoreSeederEngine: Implement Firebase Firestore Seeder Engine (FirestoreSeeder.kt): 1) Create FirestoreSeeder in com.example.songchords.firestore to seed 4 collections in Cloud Firestore: 'songs' (SuperAdmin songs), 'chords' (120 Guitar & Piano chord diagrams), 'promo_codes' (VIP-PRAYER-99, BETA-PRO-2026, BASIC-FREE-2026), and 'setlists' (sample worship setlists). 2) Add automatic seeding trigger in CloudSyncEngine and manual seed button in App settings/tools. 3) Commit and push updates to GitHub.
- **Status:** COMPLETED
- **Updates:** Task_96_ImplementFirestoreSeederEngine completed successfully. Created FirestoreSeeder.kt in com.example.songchords.firestore: 1) Populates 4 collections in Cloud Firestore: 'songs' (SuperAdmin catalog songs), 'chords' (120 Guitar and Piano chord entries), 'promo_codes' (VIP-PRAYER-99, BETA-PRO-2026, BASIC-FREE-2026), and 'setlists' (sample worship setlists). 2) Integrated automatic seeding check in CloudSyncEngine.kt and manual '🔥 Sembrar Colecciones de Firestore' button in ToolsScreen.kt and SubscriptionScreen.kt. 3) Added FirestoreSeederTest unit test suite and localized EN/ES strings. Verified with assembleDebug build and 116 passing unit tests.
- **Acceptance Criteria:**
  - FirestoreSeeder created and seeds 'songs', 'chords', 'promo_codes', and 'setlists' collections
  - Automatic and manual database seeding supported
  - build pass

### Task_97_RunAndVerifyFirestoreSeeding: Verify Firestore Seeder Engine, unit tests, and git status. Ensure zero errors.
- **Status:** COMPLETED
- **Updates:** Task_97_RunAndVerifyFirestoreSeeding completed successfully. Verified on emulator with 0 crashes. Confirmed Firestore Seeder Engine (FirestoreSeeder.kt): 1) Seeded 4 collections in Cloud Firestore: 'songs' (6 catalog songs), 'chords' (120 Guitar & Piano chord entries), 'promo_codes' (3 single-use promo codes: VIP-PRAYER-99, BETA-PRO-2026, BASIC-FREE-2026), and 'setlists' (2 worship setlists). 2) Manual seeding button '🔥 Inicializar / Sembrar Colecciones de Firestore' in ToolsScreen and SubscriptionScreen executes seeding and displays Toast feedback. All 116 unit tests pass.
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - FirestoreSeeder verified clean on emulator
- **Duration:** 2m 22s

