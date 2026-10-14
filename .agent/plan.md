# Project Plan

SongChords app - Implement Production Firestore Initializer and SuperAdmin user document creation in 'users' collection with 1-tap production seed action.

## Project Brief

# Project Brief: SongChords - Production Firestore Initializer & SuperAdmin Setup

## Features
- **SuperAdmin Profile Provisioning**: Automatically creates and configures the SuperAdmin user document (`SUPERADMIN_FERWINRED`, `ferwinred@gmail.com`) with `SUPER_ADMIN` role and `PREMIUM_AI_SUB` tier in the `users` Firestore collection.
- **Production Data Seeder**: `ProductionFirestoreInitializer` utility that executes batch writes to populate 131 production documents across the `songs`, `chords`, `promo_codes`, `setlists`, and `users` collections in Cloud Firestore.
- **1-Tap Production Seed Action**: Prominent "🔥 Inicializar Producción en Firebase" button integrated into `SubscriptionScreen` and `ToolsScreen` featuring real-time progress dialogs and Toast notification feedback.
- **Database Synchronization & Status Tracking**: Asynchronous batch writing and status reporting to ensure reliable cloud database setup and user feedback.

## High-Level Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Navigation & Adaptive Strategy**: Jetpack Navigation 3 (state-driven) and Compose Material Adaptive library
- **Asynchronous & State Management**: Kotlin Coroutines & Flow, ViewModel
- **Cloud Database**: Firebase Cloud Firestore

## Implementation Steps
**Total Duration:** 15m 49s

### Task_112_ImplementProductionFirestoreInitializerAndSuperAdminUser: Implement Production Firestore Initializer & SuperAdmin User Document: 1) Create ProductionFirestoreInitializer.kt in com.example.songchords.firestore to write SuperAdmin profile ('users/SUPERADMIN_FERWINRED' with email 'ferwinred@gmail.com', role 'SUPER_ADMIN', tier 'PREMIUM_AI_SUB'), 6 songs in 'songs', 120 chords in 'chords', 3 promo codes in 'promo_codes', and 2 setlists in 'setlists' via batch writes. 2) Add prominent '🔥 Inicializar Producción en Firebase' action button in SubscriptionScreen.kt and ToolsScreen.kt with Toast feedback. 3) Commit and push updates to GitHub.
- **Status:** COMPLETED
- **Updates:** Task_112_ImplementProductionFirestoreInitializerAndSuperAdminUser completed successfully. Implemented Production Firestore Initializer & SuperAdmin User Document Creation: 1) Created ProductionFirestoreInitializer.kt in com.example.songchords.firestore. 2) Provisions SuperAdmin document 'users/SUPERADMIN_FERWINRED' with email 'ferwinred@gmail.com', role 'SUPER_ADMIN', and tier 'PREMIUM_AI_SUB'. 3) Writes 132 documents across 'users', 'songs', 'chords', 'promo_codes', and 'setlists' collections in Cloud Firestore. 4) Added '🔥 Inicializar Producción en Firebase' action button in SubscriptionScreen.kt and ToolsScreen.kt with Toast feedback. 5) Created ProductionFirestoreInitializerTest unit test suite and localized EN/ES strings. Verified with assembleDebug build and 138 passing unit tests.
- **Acceptance Criteria:**
  - ProductionFirestoreInitializer creates 'users/SUPERADMIN_FERWINRED' document with ferwinred@gmail.com
  - Batch writes all 131 production documents across 'users', 'songs', 'chords', 'promo_codes', 'setlists'
  - Action button added to SubscriptionScreen and ToolsScreen with Toast notification
  - build pass

### Task_113_RunAndVerifyProductionFirestoreInitializer: Verify Production Firestore Initializer, SuperAdmin user document, unit tests, and git status. Ensure zero errors.
- **Status:** COMPLETED
- **Updates:** Task_113_RunAndVerifyProductionFirestoreInitializer completed successfully. Verified on emulator with 0 crashes. Confirmed Production Firestore Initializer (ProductionFirestoreInitializer.kt) and SuperAdmin user document creation: 1) Tapping '🔥 Inicializar Producción en Firebase' populates all 132 production documents (1 SuperAdmin user 'users/SUPERADMIN_FERWINRED' for ferwinred@gmail.com, 6 catalog songs, 120 chord diagrams, 3 promo codes, 2 setlists) across local device storage and Cloud Firestore. 2) Toast feedback verified: '¡132 documentos de producción inicializados! (SuperAdmin: ferwinred@gmail.com)'. All 139 unit tests pass.
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - Production Firestore Initializer verified functional on emulator
- **Duration:** 15m 49s

