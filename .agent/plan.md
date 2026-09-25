# Project Plan

SongChords app - Implement a sticky/floating performance controls bar in Song Viewer, keeping key transposition (-1/+1 Tono) and text scaling (- A / A +) controls anchored at the bottom of the screen while scrolling through song lyrics.

## Project Brief

# Project Brief: SongChords - Floating Performance Controls Bar

## Features
1. **Scrollable Song Lyrics & Chords Viewer**: Displays song lyrics alongside chord annotations with dynamic text scaling (- A / A +) support.
2. **Sticky Performance Quick Controls Bar**: A compact, semi-transparent Material 3 floating bar anchored at the bottom of the screen providing key transposition (-1 / +1 Tono) and text scaling controls.
3. **Auto-Collapsing Floating Pill**: Scroll-aware UI that automatically collapses into a compact floating pill when scrolling down to preserve maximum screen area for lyrics.
4. **Expanded Performance Options Sheet**: Bottom sheet modal accessible via 1-tap on the floating bar, offering advanced performance tools including Solfege notation, Fit to Screen, and Auto-Scroll controls.

## High-Level Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3 components
- **Navigation & Adaptive Strategy**: Jetpack Navigation 3 (state-driven) and Compose Material Adaptive library
- **Asynchronous Execution & State Management**: Kotlin Coroutines, StateFlow, and Jetpack ViewModel

## Implementation Steps
**Total Duration:** 4m 58s

### Task_41_ImplementStickyFloatingPerformanceBar: Implement Sticky / Floating Quick Controls Bar in Song Viewer (SongDetailPane.kt): 1) Position a compact, semi-transparent floating pill/bar at the bottom margin outside the scroll container so it stays anchored on screen while scrolling lyrics. 2) Include quick 1-tap actions: [-1 Tono], [Key Badge], [+1 Tono], [- A], [A +], and [🎛️ Opciones]. 3) Tapping [🎛️ Opciones] or expanding opens the full Performance Controls panel (Transposition, Solfege, Fit to Screen, Auto-Scroll). 4) Add bottom content padding (120.dp) to scroll container so song lyrics are never covered.
- **Status:** COMPLETED
- **Updates:** Task_41_ImplementStickyFloatingPerformanceBar completed successfully. Implemented Sticky / Floating Quick Controls Bar in SongDetailPane.kt: 1) Positioned a compact semi-transparent rounded pill Surface (RoundedCornerShape(28.dp), surfaceContainerHigh.copy(alpha=0.95f), 6.dp elevation) anchored at Alignment.BottomCenter outside the scroll container so it stays floating while scrolling. 2) Integrated 1-tap quick performance controls: [-1 Tono], [Key Badge], [+1 Tono], [- A], [Scale % Badge], [A +], and [🎛️ Tune] expand options button. 3) Added 130.dp bottom padding to scroll container so lyrics are never covered. Verified with assembleDebug and 61 passing unit tests.
- **Acceptance Criteria:**
  - Key transposition (-1/+1 Tono) and text scaling (- A / A +) stay anchored and accessible at bottom of screen while scrolling
  - Tapping options chip or expanding opens full performance controls sheet
  - Scroll container has bottom padding so lyrics are never covered
  - build pass

### Task_42_RunAndVerifyStickyFloatingBar: Verify sticky/floating performance controls bar on emulator with critic_agent. Ensure clean scrolling, zero crashes, and immediate tool accessibility.
- **Status:** COMPLETED
- **Updates:** Task_42_RunAndVerifyStickyFloatingBar completed successfully. Verified on emulator with 0 crashes. Confirmed sticky/floating quick performance controls bar stays anchored at bottom of screen while scrolling through lyrics. Verified 1-tap transposition (-1 / +1 Tono), 1-tap text scaling (- A / A +), and expand options tune button. Confirmed bottom lyrics are fully visible above 130.dp scroll margin. All 61 unit tests pass.
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - Sticky/floating quick controls bar verified clean and functional on emulator
- **Duration:** 4m 58s

