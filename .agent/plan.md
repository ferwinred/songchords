# Project Plan

SongChords app - Add dynamic Light/Dark Theme mode with one-tap toggle button in the top app bar, providing seamless runtime switching between Material 3 Light Theme and Dark Theme.

## Project Brief

# SongChords - Project Brief

## Features
- **Dynamic Theme Switching**: Seamless, runtime switching between Material 3 Light Theme (bright light surfaces with high-contrast dark text/chords) and Dark Theme (deep dark background surfaces with bright chord badges) across all screens.
- **TopAppBar Theme Toggle**: One-tap theme toggle icon button (☀️ Light Mode / 🌙 Dark Mode) placed in the TopAppBar alongside language selection for instant accessibility.
- **Theme Preference Persistence**: Stores user theme selection using Preferences DataStore so the app retains the chosen Light or Dark mode preference across restarts.
- **High-Contrast Lyric & Chord Viewer**: Renders song lyrics and interactive chord overlays with dynamic color palettes tailored for optimal readability in both light and dark environments.

## High-Level Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3 Design
- **Navigation**: Jetpack Navigation 3 (state-driven)
- **Adaptive Strategy**: Compose Material Adaptive library
- **Asynchronous & State Management**: Kotlin Coroutines, StateFlow, ViewModel
- **Preference Persistence**: Jetpack DataStore (Preferences) for storing theme preferences

## Implementation Steps
**Total Duration:** 6m 12s

### Task_31_AddLightDarkThemeToggle: Implement dynamic Light/Dark Theme toggle mode: 1) Create ThemeMode enum (LIGHT, DARK, SYSTEM) and ThemeState holder in com.example.songchords.ui.theme. 2) Update SongChordsTheme composable to dynamically apply Light/Dark Material 3 color schemes. 3) Add ThemeToggleIconButton in TopAppBar in SongListDetailScreen.kt. 4) Ensure high-contrast chord badges, text, and cards in both Light and Dark themes.
- **Status:** COMPLETED
- **Updates:** Task_31_AddLightDarkThemeToggle completed successfully. Implemented dynamic Light/Dark Theme toggle mode: 1) Created ThemeMode enum (LIGHT, DARK, SYSTEM) and high-contrast LightColorScheme & DarkColorScheme in Theme.kt. 2) Created ThemeToggleIconButton displaying Sun icon in Dark mode and Moon icon in Light mode. 3) Added ThemeToggleIconButton to TopAppBar actions in SongListDetailScreen next to LanguageSelector. 4) Added localized theme_toggle strings in English and Spanish. 5) Added ThemeModeTest unit test. Verified with assembleDebug and 37 passing unit tests.
- **Acceptance Criteria:**
  - Theme toggle icon button (Light/Dark) added to top app bar next to LanguageSelector
  - Toggling theme button switches app dynamically between Light and Dark mode across all screens
  - High-contrast readability for chord badges and lyrics in both Light and Dark themes
  - build pass

### Task_32_RunAndVerifyThemeToggle: Verify Light/Dark theme switching on emulator with critic_agent. Ensure zero crashes and high contrast in both Light and Dark modes.
- **Status:** COMPLETED
- **Updates:** Task_32_RunAndVerifyThemeToggle completed successfully. Verified on emulator with 0 crashes. Confirmed top app bar theme toggle button (Sun/Moon icon next to LanguageSelector) instantly switches app between Light Theme and Dark Theme across all screens (Song List, Song Viewer, Chord Diagrams, Song Editor). Verified high contrast and legibility in both themes. All 37 unit tests pass.
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - Light and Dark theme switching verified clean and functional on emulator
- **Duration:** 6m 12s

