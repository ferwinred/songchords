# Project Plan

SongChords app - Unify TopAppBar (eliminate duplicate stacked headers), make Bottom Navigation Bar icon-only and keyboard-aware, and reorganize Song Editor metadata fields (full-width Tags field, 2-column Key/Time Sig & Tempo/Notes).

## Project Brief

# SongChords - Project Brief

## Features
- **Unified Top Navigation Header**: A single, non-stacked `TopAppBar` across all screens featuring a Dark Navy background (`#020873` Light / `#1E2558` Dark), single-line title with ellipsis truncation ("Acordes Cristianos y Alabanzas"), and icon-only action buttons (Cloud, Tools, Import, Theme, Language).
- **Keyboard-Aware Icon-Only Bottom Navigation**: Clean, icon-only bottom navigation bar (Songs 🎵, Tuner 🎸, Chord Library 🎹, New Song ➕) without text labels that automatically hides when the soft keyboard is active to prevent vertical screen squashing.
- **Structured Song Editor Form**: Reorganized metadata card UI featuring spacious full-width rows (Title, Artist, Tags) and 2-column grid rows (Key & Time Signature, Tempo BPM & Notes/Comments) ensuring zero element clipping or squashing.
- **Song & Chord Library View**: Centralized screen layout for managing, editing, and displaying Christian songs and chord sheets.

## High-Level Technical Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Navigation Strategy**: Jetpack Navigation 3 (state-driven navigation)
- **Adaptive Layouts**: Compose Material Adaptive library (`androidx.compose.material3.adaptive`)
- **Async & State Management**: Kotlin Coroutines & StateFlow
- **System Insets Handling**: Compose Window Insets (`WindowInsets.ime` for soft keyboard reactivity)

## Implementation Steps
**Total Duration:** 16m 50s

### Task_69_FixTopBarBottomBarAndSongEditorLayout: Fix 3 Critical UI/UX Bugs from Screenshots: 1) Unify TopAppBar into a SINGLE non-stacked header bar across all screens (eliminate duplicate purple top bar), with brand Dark Navy background (#020873 Light / #1E2558 Dark), single-line title ('Acordes Cristianos y Alabanzas'), and icon-only action buttons. 2) Make Bottom Navigation Bar icon-only without text labels (label = null, alwaysShowLabel = false) and hide when soft keyboard is open (WindowInsets.ime). 3) Reorganize Song Editor metadata card into full-width Title, Artist, Tags, and 2-column Key/Time Sig & Tempo/Notes fields.
- **Status:** COMPLETED
- **Updates:** Task_69_FixTopBarBottomBarAndSongEditorLayout completed successfully. Resolved 3 critical UI/UX issues from user screenshots: 1) Unified TopAppBar into a single clean non-stacked header bar across all screens with single-line title ('Acordes Cristianos y Alabanzas' with TextOverflow.Ellipsis) and Brand Dark Navy background (#020873 Light / #1E2558 Dark). 2) Made Bottom Navigation Bar icon-only (label = null, alwaysShowLabel = false) and keyboard-aware (hides automatically when WindowInsets.ime soft keyboard is active). 3) Reorganized Song Editor metadata card into 5 spacious rows (full-width Title, Artist, Tags, and 2-column Key/Time Sig & Tempo/Notes fields). Verified with assembleDebug build and 66 passing unit tests.
- **Acceptance Criteria:**
  - TopAppBar is unified into a single clean header without double stacking
  - Bottom Navigation Bar is icon-only and hides when soft keyboard is active
  - Song Editor Tags field has full-width layout and never squashes or wraps placeholder text awkwardly
  - build pass

### Task_70_RunAndVerifyUIFixesScreenshots: Verify unified top bar, icon-only/keyboard-aware bottom bar, and reorganized song editor on emulator with critic_agent. Ensure zero crashes.
- **Status:** COMPLETED
- **Updates:** Task_70_RunAndVerifyUIFixesScreenshots completed successfully. Verified on device/emulator with 0 crashes. Confirmed all screenshot UI fixes: 1) Native purple ActionBar disabled in themes.xml (parent=NoActionBar), leaving strictly ONE Compose TopAppBar with crisp white title 'Acordes Cristianos y Alabanzas' on Brand Dark Navy background (#020873 / #1E2558). 2) Bottom Navigation Bar is icon-only without text labels (label = null, alwaysShowLabel = false) and keyboard-aware (hides automatically when soft keyboard is active). 3) Reorganized Song Editor metadata card with 2-column rows for Key/Time Sig & Tempo/Notes, full-width Tags/Etiquetas field at the bottom, and 24.dp bottom padding. All 66 unit tests pass.
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - Unified top bar, icon-only bottom bar, and song editor layout verified clean on emulator
- **Duration:** 16m 50s

