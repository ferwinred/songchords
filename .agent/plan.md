# Project Plan

SongChords app - Implement persistent local database on user device synced with Firebase Firestore for online cloud updates and 100% offline access.

## Project Brief

# Project Brief: SongChords

## Features
1. **Persistent Local Device Storage**: Store songs, setlists, and chord sheets locally using Room database to ensure seamless 100% offline access and instant data persistence across app restarts.
2. **Firebase Cloud Synchronization**: Synchronize real-time remote updates from Firebase Firestore with the local database whenever online, merging changes automatically.
3. **Song & Chord Sheet Viewer**: Browse, display, and transpose song chord sheets with reactive data flows driven by local-first repositories.
4. **Setlist Management**: Organize songs into custom setlists for live performances and practice sessions, available both offline and in sync with the cloud.

## High-Level Tech Stack
* **Language**: Kotlin
* **UI Framework**: Jetpack Compose
* **Navigation**: Jetpack Navigation 3 (state-driven)
* **Adaptive Layouts**: Compose Material Adaptive library
* **Asynchronous Streams**: Kotlin Coroutines & Flow
* **Local Persistence**: Room Database
* **Cloud Database & Sync**: Firebase Firestore
* **Architecture**: Repository Pattern (`SongRepository`, `SetlistRepository`, `FirestoreChordLibraryRepository`)

## Implementation Steps
**Total Duration:** 4m 50s

### Task_100_ImplementPersistentLocalDatabaseAndFirebaseSync: Implement Persistent Local Database on User Device synced with Firebase Firestore: 1) Implement LocalPersistentDatabase for songs, setlists, and chords stored in local JSON/SQLite storage on user device. 2) Update CloudSyncEngine, CloudSyncRepository, and SetlistRepository to load from local device database when offline and sync/merge with Firebase Firestore when online. 3) Ensure custom songs and setlists created by users persist locally on device across app restarts.
- **Status:** COMPLETED
- **Updates:** Task_100_ImplementPersistentLocalDatabaseAndFirebaseSync completed successfully. Implemented Persistent Local Storage on user device synced with Firebase Firestore: 1) Created LocalPersistentStorage in com.example.songchords.repository.local to manage internal JSON file storage (songs_db.json and setlists_db.json) in context.filesDir. 2) Updated LocalSongRepository and LocalSetlistRepository to load from and persist to local device storage across app restarts. 3) Updated CloudSyncEngine.kt and CloudSyncRepository.kt to fetch/merge remote Firestore data with local storage when online and serve directly from device storage when offline. 4) Created LocalPersistentStorageTest unit test suite. Verified with assembleDebug build and 123 passing unit tests.
- **Acceptance Criteria:**
  - Songs, setlists, and chords stored in persistent local database on user device
  - Data persists locally across app restarts and is available 100% offline
  - Firebase Firestore syncs with local database when online
  - build pass

### Task_101_RunAndVerifyPersistentLocalDbAndSync: Verify persistent local database on user device and Firebase Firestore synchronization on emulator with critic_agent. Ensure zero crashes.
- **Status:** COMPLETED
- **Updates:** Task_101_RunAndVerifyPersistentLocalDbAndSync completed successfully. Verified on emulator with 0 crashes. Confirmed persistent local storage on user device and Firebase Firestore synchronization: 1) Created custom song 'Cancion Local Persistente' and setlist 'Lista Local Persistente'. 2) Force closed and relaunched app: verified data persists 100% locally across restarts in context.filesDir (songs_db.json and setlists_db.json). 3) Confirmed online cloud sync with Firebase Firestore. All 123 unit tests pass.
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - Persistent local database and Firebase sync verified functional on emulator
- **Duration:** 4m 50s

