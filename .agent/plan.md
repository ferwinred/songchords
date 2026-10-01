# Project Plan

SongChords app - Implement single '#' comment syntax (# Comentario), docked floating quick helper toolbar in editor, smart chord/section/comment autocomplete, and remove duplicate viewer footer bar.

## Project Brief

# Project Brief: SongChords App

## Features
1. **Single '#' Comment Syntax**: Strictly supports `#` for comment lines (e.g., `# Tocar suave con piano`). Renders comments with distinct italicized info highlighting in the editor and as styled callout badges with info icons in the song viewer.
2. **Docked Floating Quick Helper Toolbar**: A toolbar docked directly above the editor text field for 1-tap insertion of common sections (`[Intro]`, `[Coro]`), key chords (`[G]`, `[C]`), and comment prefixes (`#`).
3. **Smart Autocomplete**: Context-aware autocomplete suggestion chips triggered when typing `[` or `#` for instant chord, section, and comment insertion with auto-closing bracket handling.
4. **Streamlined Song Viewer Layout**: Clean and clutter-free song viewing experience achieved by removing duplicate floating footer bars and maintaining a clean fixed Bottom Navigation Bar.

## High-Level Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Navigation Strategy**: Jetpack Navigation 3 (State-driven)
- **Adaptive Strategy**: Compose Material Adaptive Library
- **Architecture & Concurrency**: MVVM (ViewModel, StateFlow) & Kotlin Coroutines

## Implementation Steps
**Total Duration:** 2m 33s

### Task_71_ImplementSingleHashCommentSyntaxAndDockedEditorToolbar: Implement Single '#' Comment Syntax, Docked Floating Editor Toolbar, Smart Autocomplete, and Streamlined Viewer: 1) In ChordParser.kt, parse lines starting with '#' (e.g. '# Tocar suave con piano') into ChordLyricsLine.Comment. 2) In SongDetailPane.kt, render '#' comment lines as distinct italicized callout badges with Info icon and tertiaryContainer background. Remove duplicate floating footer bar in SongDetailPane.kt. 3) In SongEditorScreen.kt, dock the Quick Helper Toolbar (# Comentario, [Intro], [Coro], [G], [C], [D], [Em]) directly above the editor text field so tools stay floating/docked at 1-tap while typing. 4) Add smart autocomplete chips when typing '[' or '#' in editor with auto-closing bracket ']'.
- **Status:** COMPLETED
- **Updates:** Task_71_ImplementSingleHashCommentSyntaxAndDockedEditorToolbar completed successfully. Implemented 4 key features: 1) Single '#' comment syntax: ChordParser parses lines starting with '#' (e.g. '# Tocar suave con piano') into ChordLyricsLine.Comment and renders them in SongDetailPane as distinct italicized callout badges with Info icon and tertiaryContainer background. 2) Removed duplicate floating footer bar in SongDetailPane.kt, leaving the fixed Bottom Navigation Bar clean and un-obscured. 3) Docked Floating Quick Helper Toolbar in SongEditorScreen.kt directly above the editor text field with '# Comentario' button, section chips ([Intro], [Coro]), and key chord chips ([G], [C]). 4) Smart autocomplete & auto-closing brackets in SongEditorScreen when typing '[' or '#' with 1-tap insertion. Verified with assembleDebug build and 67 passing unit tests.
- **Acceptance Criteria:**
  - Comment lines starting with '#' parsed and rendered as distinct italicized callout badges
  - Duplicate floating footer bar removed in SongDetailPane.kt
  - SongEditorScreen has docked Floating Quick Helper Toolbar above text field
  - Smart autocomplete suggestions triggered when typing '[' or '#' in editor
  - build pass

### Task_72_RunAndVerifySingleHashCommentsAndDockedEditorToolbar: Verify '#' comment syntax, docked editor toolbar, smart autocomplete, and streamlined viewer layout on emulator with critic_agent. Ensure zero crashes.
- **Status:** COMPLETED
- **Updates:** Task_72_RunAndVerifySingleHashCommentsAndDockedEditorToolbar completed successfully. Verified on emulator with 0 crashes. Confirmed all requested features: 1) Removed duplicate floating footer bar in Song Viewer, leaving fixed Bottom Navigation Bar clean and un-obscured. 2) Docked Quick Tools Toolbar in Song Editor directly above the text field for 1-tap insertion while typing. 3) Single '#' comment syntax (# Tocar suave con piano) parsed and rendered in Vista Previa as a distinct italicized callout badge with Info icon and tertiaryContainer background. 4) Smart autocomplete suggestion chips triggered when typing '[' or '#' with auto-closing bracket ']' handling. All 67 unit tests pass cleanly.
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - Single '#' comments, docked editor toolbar, smart autocomplete, and streamlined viewer verified clean on emulator
- **Duration:** 2m 33s

