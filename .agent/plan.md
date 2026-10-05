# Project Plan

SongChords app - Streamline TwoTierHeader.kt to eliminate top padding gap above 'SongChords' title and reduce overall header height for a sleek, compact, and space-saving header.

## Project Brief

# Project Brief: SongChords - Streamlined Two-Tier Header

## Features
- **Compact Two-Tier Header Layout**: Redesigned `TwoTierHeader.kt` featuring a sleek Level 1 Title Banner and Level 2 Tools Bar with tight vertical padding.
- **Top Gap Elimination**: Reduced top padding above the 'SongChords' title to sit close to the top screen edge, eliminating awkward empty vertical space.
- **Compact Level 1 Title Banner**: Minimal padding (top = 4.dp, bottom = 4.dp) featuring bold title typography.
- **Compact Level 2 Tools Action Bar**: Tight horizontal row (2.dp vertical padding) placing icon action buttons directly underneath the title banner.
- **Maximized Viewport Area**: Significantly reduced overall header height to maximize real estate for chord sheets and song lists.

## High-Level Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Navigation**: Jetpack Navigation 3 (State-driven)
- **Adaptive Strategy**: Compose Material Adaptive Library
- **Concurrency & Async**: Kotlin Coroutines & Flow

## Implementation Steps
**Total Duration:** 1m 12s

### Task_91_CompactTwoTierHeaderAndEliminateTopGap: Compact TwoTierHeader.kt and eliminate top gap above 'SongChords' title: 1) Reduce top padding above 'SongChords' title in TwoTierHeader.kt (using statusBarsPadding() with tight 4.dp inner top padding). 2) Reduce vertical padding in Level 1 Title Banner (4.dp) and Level 2 Tools Action Bar (2.dp). 3) Commit and push updates to GitHub.
- **Status:** COMPLETED
- **Updates:** Task_91_CompactTwoTierHeaderAndEliminateTopGap completed successfully. Compacted TwoTierHeader.kt and eliminated top gap above 'SongChords' title: 1) Applied statusBarsPadding() once on root Column of TwoTierHeader. 2) Reduced Level 1 Title Banner padding to top=4.dp, bottom=2.dp so 'SongChords' title sits cleanly near top edge. 3) Reduced Level 2 Tools Action Bar vertical padding to 2.dp. 4) Compacted total header height to save screen space. Verified with assembleDebug build and 92 passing unit tests.
- **Acceptance Criteria:**
  - Top gap above 'SongChords' title eliminated
  - TwoTierHeader vertical height compacted and sleek
  - 'SongChords' title sits comfortably near top edge
  - build pass

### Task_92_RunAndVerifyCompactHeaderLayout: Verify compact TwoTierHeader layout on emulator with critic_agent. Ensure zero crashes and sleek visual proportions.
- **Status:** COMPLETED
- **Updates:** Task_92_RunAndVerifyCompactHeaderLayout completed successfully. Verified on emulator with 0 crashes. Confirmed compact TwoTierHeader layout: 1) Large empty gap above 'SongChords' title is eliminated and title sits cleanly near top status bar. 2) Total header height is compact and sleek, saving screen space. 3) Level 2 tools bar (Cloud Sync, Subscriptions, Import JSON, Theme, Language) sits tightly underneath Level 1 title. All 92 unit tests pass.
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - Compact TwoTierHeader layout verified clean on emulator
- **Duration:** 1m 12s

