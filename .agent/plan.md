# Project Plan

SongChords app - Implement 2-tier header layout separating App Title 'SongChords - Acordes Cristianos' (Level 1 top banner) from Action Tools Bar (Level 2 row below title header) across all screens.

## Project Brief

# Project Brief: SongChords - Acordes Cristianos

## Features

1. **2-Tier Header Layout**: Consistent 2-tier vertical header system across all core screens separating the Level 1 top title banner ("SongChords - Acordes Cristianos" in large bold white text on Brand Dark Navy `#020873` / `#1E2558`) from the Level 2 tool controls.
2. **Action Tools Bar**: Direct-access horizontal controls bar positioned directly under the title header featuring action buttons for Cloud Sync (☁️), Subscriptions (⭐), Import JSON (📥), Light/Dark Theme toggle (☀️/🌙), and Language Selector (🌐).
3. **Song Library & Detail View (`SongListDetailScreen`)**: Browse and view christian song chords with an adaptive multi-pane layout structure.
4. **Song Editor (`SongEditorScreen`)**: Create, modify, and manage chord charts and metadata within a seamless editing view.
5. **Tools & Utilities Hub (`ToolsScreen`)**: Centralized screen for managing application preferences, theme toggling, language options, and data import operations.

## High-Level Technical Stack

- **Language**: Kotlin
- **UI Toolkit**: Jetpack Compose & Material 3
- **Navigation**: Jetpack Navigation 3 (State-driven navigation)
- **Adaptive Strategy**: Compose Material Adaptive library (`androidx.compose.material3.adaptive`)
- **Asynchronous & Reactive State**: Kotlin Coroutines & Flow
- **Architecture**: Modern Android Architecture with Unidirectional Data Flow (UDF) & ViewModels

## Implementation Steps
**Total Duration:** 1m 39s

### Task_89_Implement2TierHeaderLayout: Implement 2-Tier Header Layout in SongListDetailScreen.kt, ToolsScreen.kt, and SongEditorScreen.kt: 1) Level 1 (Top Banner): App Title 'SongChords - Acordes Cristianos' in large bold white text (#FFFFFF) on Brand Dark Navy background (#020873 Light / #1E2558 Dark). 2) Level 2 (Tools Bar directly below Title): Horizontal tools bar containing Cloud Sync (☁️), Subscriptions (⭐), Import JSON (📥), Theme Toggle (☀️/🌙), and Language Selector (🌐) icon buttons. 3) Commit and push updates to GitHub.
- **Status:** COMPLETED
- **Updates:** Task_89_Implement2TierHeaderLayout completed successfully. Implemented 2-Tier Header Layout across SongListDetailScreen.kt, ToolsScreen.kt, and SongEditorScreen.kt: 1) Level 1 (Top Title Header Banner): Full-width title banner displaying stringResource(R.string.app_name) ('SongChords - Acordes Cristianos') in large bold white text (#FFFFFF) on Brand Dark Navy background (#020873 Light / #1E2558 Dark). 2) Level 2 (Action Tools Bar directly below Title Header): Clean horizontal tools bar containing all 5 action buttons: CloudSyncButton (☁️), SubscriptionButton (⭐), ImportJsonButton (📥), ThemeToggleIconButton (☀️/🌙), and LanguageSelector (🌐). Verified with assembleDebug build and 92 passing unit tests.
- **Acceptance Criteria:**
  - App Title 'SongChords - Acordes Cristianos' displayed prominently in top level header banner
  - Tools action bar (Cloud Sync, Subscriptions, Import JSON, Theme, Language) positioned cleanly below the title header
  - Applied across SongListDetailScreen, ToolsScreen, and SongEditorScreen
  - build pass

### Task_90_RunAndVerify2TierHeaderLayout: Verify 2-tier header layout on emulator with critic_agent. Ensure zero crashes and clean visual structure.
- **Status:** COMPLETED
- **Updates:** Task_90_RunAndVerify2TierHeaderLayout completed successfully. Verified on emulator with 0 crashes. Confirmed 2-tier vertical header layout across all screens (SongListDetailScreen, SongEditorScreen, ToolsScreen): 1) Level 1 (Top Title Header Banner): App Title 'SongChords - Acordes Cristianos' displayed prominently in large bold white text (#FFFFFF) on Brand Dark Navy background (#020873 Light / #1E2558 Dark). 2) Level 2 (Tools Action Bar directly below Title Header): Horizontal tools bar positioned directly underneath Level 1 containing all 5 action buttons (Cloud Sync ☁️, Subscriptions ⭐, Import JSON 📥, Theme Toggle ☀️/🌙, Language Selector 🌐). All 92 unit tests pass cleanly.
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - 2-tier header layout verified clean and functional on emulator
- **Duration:** 1m 39s

