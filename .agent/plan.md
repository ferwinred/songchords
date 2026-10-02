# Project Plan

SongChords app - Render app_name ('SongChords - Acordes Cristianos') in TopAppBar title, and fix deformation on '# Comentario' button in SongEditorScreen quick tools.

## Project Brief

# Project Brief: SongChords

## Features
- **Song List & Detail Navigation**: Browse and select praise and worship song chord sheets seamlessly using responsive dual-pane views.
- **Localized Top Bar Branding**: Dynamically display localized app titles (`SongChords - Acordes Cristianos` in Spanish, `SongChords - Praise & Worship` in English) in the main top app bar.
- **Song Sheet Editor & Quick Tools**: Create and edit chord sheets with dedicated quick-tool actions, featuring a proportioned `# Comentario` button with dedicated icon and single-line layout.
- **Multi-Language Localization**: Full string resource localization for Spanish and English locales.

## High-Level Technical Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Navigation**: Jetpack Navigation 3 (state-driven)
- **Adaptive Strategy**: Compose Material Adaptive library
- **Architecture & Async**: ViewModel, StateFlow, and Kotlin Coroutines

## Implementation Steps
**Total Duration:** 3m 31s

### Task_79_FixTopBarAppNameAndEditorCommentButton: Fix TopAppBar Title and Editor Comment Button Shape: 1) In SongListDetailScreen.kt and TopAppBar composables, use stringResource(R.string.app_name) ('SongChords - Acordes Cristianos' in Spanish, 'SongChords - Praise & Worship' in English) as the title. 2) In SongEditorScreen.kt, redesign the '# Comentario' button with explicit 36.dp height, singleLine text, clean padding, and comment icon so it is perfectly proportioned, aligned, and never deformed.
- **Status:** COMPLETED
- **Updates:** Task_79_FixTopBarAppNameAndEditorCommentButton completed successfully. Resolved both requested fixes: 1) Updated TopAppBar title to use stringResource(R.string.app_name) ('SongChords - Acordes Cristianos' in Spanish, 'SongChords - Praise & Worship' in English). 2) Redesigned the '# Comentario' button in SongEditorScreen.kt with explicit 36.dp height, maxLines = 1, singleLine text, Comment leading icon, tertiaryContainer background styling, and horizontal scroll container so all quick tool buttons (🎵 Acordes, 📑 Secciones, # Comentario) are perfectly proportioned, aligned, and never deformed. Verified with assembleDebug build and 67 passing unit tests.
- **Acceptance Criteria:**
  - TopAppBar title renders R.string.app_name ('SongChords - Acordes Cristianos')
  - '# Comentario' button in SongEditorScreen is perfectly proportioned and non-deformed
  - build pass

### Task_80_RunAndVerifyAppNameAndCommentButtonFix: Verify R.string.app_name top bar title and non-deformed comment button on emulator with critic_agent. Ensure zero crashes.
- **Status:** COMPLETED
- **Updates:** Task_80_RunAndVerifyAppNameAndCommentButtonFix completed successfully. Verified on emulator with 0 crashes. Confirmed: 1) TopAppBar title displays R.string.app_name ('SongChords - Acordes Cristianos' in Spanish, 'SongChords - Praise & Worship' in English). 2) In SongEditorScreen.kt, the '# Comentario' button is clean, perfectly proportioned, aligned alongside '🎵 Acordes' and '📑 Secciones', and NOT deformed or squashed. Tapping inserts '# ' at current cursor location. All 67 unit tests pass cleanly.
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - R.string.app_name title and non-deformed comment button verified clean on emulator
- **Duration:** 3m 31s

