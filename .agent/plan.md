# Project Plan

SongChords app - Implement SuperAdmin delete permissions for all songs/setlists, direct Firestore seed trigger with toast logging, and Firebase Console setup guide.

## Project Brief

# Project Brief: SongChords App

## Features
1. **SuperAdmin Delete Override**: Grant administrative override permissions to `ferwinred@gmail.com`, enabling full deletion authority for any song or setlist across the app UI and Firestore security rules (`firestore.rules`).
2. **Direct Firestore Seed Trigger & Toast Logging**: Include an easily accessible *"🔥 Sembrar Firebase Firestore Ahora"* action button in the TopAppBar / ToolsScreen / SubscriptionScreen that forces a direct write of 131 initial documents across `songs`, `chords`, `promo_codes`, and `setlists` collections, providing real-time Toast feedback with document counts and error reporting.
3. **Song & Setlist Catalog Management**: View, browse, and organize songs, chord sheets, setlists, and promotional codes with role-based access control.
4. **Firebase Setup & Configuration**: Integrated setup and configuration support for Cloud Firestore in Test Mode and custom security rules for administrative overrides.

## High-Level Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Navigation & Adaptive Strategy**: Jetpack Navigation 3 (state-driven) and Compose Material Adaptive library
- **Asynchronous Processing**: Kotlin Coroutines & Flow
- **Backend Services**: Firebase Cloud Firestore SDK, Firebase Authentication
- **Architecture**: MVVM (Model-View-ViewModel) Architecture

## Implementation Steps
**Total Duration:** 6m 52s

### Task_102_ImplementSuperAdminDeleteOverrideAndDirectFirestoreSeedTrigger: Implement SuperAdmin Delete Override & Direct Firestore Seed Trigger: 1) Update Song.isEditableBy(), Setlist.isDeletableBy(), and firestore.rules to allow SuperAdmin (ferwinred@gmail.com / SUPERADMIN_FERWINRED) to delete any song or setlist. 2) Add a '🔥 Sembrar Firestore' action button in TopAppBar / ToolsScreen / SubscriptionScreen that forces direct write of all 131 documents to Firestore 'songs', 'chords', 'promo_codes', and 'setlists' collections, showing Toast diagnostics. 3) Commit and push updates to GitHub.
- **Status:** COMPLETED
- **Updates:** Task_102_ImplementSuperAdminDeleteOverrideAndDirectFirestoreSeedTrigger completed successfully. Implemented SuperAdmin Delete Override and Direct Firestore Seed Trigger: 1) Updated Song.isEditableBy(), Setlist.isDeletableBy(), SongDetailPane.kt, SongListDetailScreen.kt, SetlistScreen.kt, and firestore.rules so SuperAdmin (ferwinred@gmail.com / SUPERADMIN_FERWINRED) can delete any song or setlist across the app and Cloud Firestore. 2) Added '🔥 Sembrar Base de Datos Firestore' action button in SubscriptionScreen.kt and ToolsScreen.kt with Toast error diagnostics to write all 131 documents directly to Cloud Firestore on demand. Verified with assembleDebug build and 125 passing unit tests.
- **Acceptance Criteria:**
  - SuperAdmin can delete any song or setlist in UI and firestore.rules
  - Direct Firestore seed action button added with Toast error diagnostics
  - build pass

### Task_103_RunAndVerifySuperAdminDeleteAndFirestoreSeed: Verify SuperAdmin delete override, direct Firestore seed button, and Toast diagnostics on emulator with critic_agent. Ensure zero crashes.
- **Status:** COMPLETED
- **Updates:** Task_103_RunAndVerifySuperAdminDeleteAndFirestoreSeed completed successfully. Verified on emulator with 0 crashes. Confirmed SuperAdmin delete override (SuperAdmin ferwinred@gmail.com sees Delete button for all songs/setlists) and direct Firestore seed action button '🔥 Sembrar Base de Datos Firestore' in SubscriptionScreen and ToolsScreen triggering direct write of 131 documents with Toast diagnostics. All 125 unit tests pass.
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - SuperAdmin delete override and direct Firestore seed action verified on emulator
- **Duration:** 6m 52s

