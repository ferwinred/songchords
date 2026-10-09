# Project Plan

SongChords app - Verify Firebase integration via app/google-services.json and provide step-by-step guide for Firebase Console database & authentication initialization.

## Project Brief

# Project Brief: SongChords App

## Features
1. **Authentication & User Profiles**: Secure access via Firebase Authentication supporting both Google Sign-In and Anonymous guest modes.
2. **Song & Chord Library**: Real-time browsing, viewing, and searching of songs and chord sheets synced live with Cloud Firestore.
3. **Setlist Management**: Create, organize, and manage custom setlists for practice sessions and live performances.
4. **Promo Code Redemption**: Unlock feature access or premium content by redeeming promotional codes verified against Cloud Firestore.

## High-Level Technical Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Navigation & Adaptive Strategy**: Jetpack Navigation 3 (state-driven) and Compose Material Adaptive library
- **Asynchronous & Reactive Streams**: Kotlin Coroutines & Flow
- **Backend & Cloud Services**: Firebase Authentication SDK & Cloud Firestore SDK (`songs`, `chords`, `promo_codes`, `setlists`)

## Implementation Steps

### Task_106_VerifyFirebaseIntegrationAndGuide: Verify Firebase App-Side Integration & Provide Console Setup Guide: 1) Confirm app/google-services.json, FirebaseAuthManager, CloudSyncEngine, and FirestoreSeeder are connected to Firebase SDK. 2) Provide step-by-step setup guide for Firebase Console (Firestore Database creation, firestore.rules publishing, Google & Anonymous Auth activation). 3) Commit and push updates to GitHub.
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - App-side Firebase SDK integration verified clean
  - Firebase Console setup guide provided
  - build pass

### Task_107_RunAndVerifyFirebaseIntegration: Verify Firebase integration and unit tests on emulator with critic_agent. Ensure zero crashes.
- **Status:** PENDING
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - Firebase integration verified clean on emulator

