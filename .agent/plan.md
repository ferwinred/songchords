# Project Plan

SongChords app - Implement Transposed Setlists with key overrides, 4-Tier Membership System (Guest, Free Registered, Basic, Pro, Premium AI), Subscription Manager, and Gemini AI Music Assistant for instrument learning.

## Project Brief

# SongChords - Project Brief

## Features

1. **Transposed Custom Setlists**: Create and manage custom setlists (e.g., 'Domingo', 'Adoración') with individual song key overrides (e.g., transposing from key G to key C).
2. **Transposed Setlist Song Viewer**: Interactive viewer displaying chords and lyrics with real-time key transposition tailored to setlist preferences.
3. **Multi-Tier Membership & Subscription Manager**: Enforce usage limits and feature gates across 5 user tiers (Guest, Registered Free, Basic, Pro, and Premium AI) with built-in subscription management.
4. **Gemini AI Music Assistant**: AI-powered conversational interface leveraging the Gemini API to provide guitar and piano guidance, chord progression recommendations, music theory explanations, and app navigation assistance.

## High-Level Technical Stack

* **Language**: Kotlin
* **UI Framework**: Jetpack Compose with Material Design 3
* **Navigation & Adaptive Strategy**: Jetpack Navigation 3 (state-driven) and Compose Material Adaptive library for adaptive layouts across device form factors
* **Architecture & Async**: ViewModel, StateFlow, Kotlin Coroutines, and Flow
* **AI Integration**: Google Generative AI SDK (Gemini API)

## Implementation Steps
**Total Duration:** 26m 49s

### Task_83_ImplementTransposedSetlistsDomainAndRepository: Implement Transposed Setlists Domain Model & Repository: 1) Create Setlist and SetlistSong models supporting transposedKey override per song. 2) Implement SetlistRepository with local offline persistence and cloud sync. 3) Build SetlistScreen UI for creating, editing, and organizing setlists. 4) Support opening setlist songs in SongDetailPane automatically transposed to the setlist's saved key override.
- **Status:** COMPLETED
- **Updates:** Task_83_ImplementTransposedSetlistsDomainAndRepository completed successfully. Implemented Transposed Setlists Domain, Repository, and UI: 1) Created Setlist and SetlistSong models supporting transposedKey overrides per song. 2) Built SetlistRepository and LocalSetlistRepository reactive flow implementation pre-populated with sample setlists. 3) Built SetlistScreen.kt, SetlistViewModel.kt, and SetlistDialogs.kt. 4) Added Setlists tab (📋 Listas / Setlists) in Bottom Navigation Bar. 5) Integrated 'Add to Setlist' action in SongDetailPane.kt and SongListItem.kt allowing users to save custom key overrides (e.g. Original G ➔ Saved C). Opening songs from setlists automatically applies the saved transposed key override. 6) Added SetlistRepositoryTest unit test suite and localized EN/ES strings. Verified with assembleDebug build and 71 passing unit tests.
- **Acceptance Criteria:**
  - Setlist and SetlistSong models created with transposedKey override support
  - SetlistRepository implemented for CRUD operations
  - SetlistScreen UI built for setlist management
  - Songs open in SongDetailPane with transposed key override applied
  - build pass

### Task_84_ImplementMembershipTiersAndSubscriptionManager: Implement 5-Tier Membership System & Subscription Manager: 1) Define UserTier enum (ANONYMOUS, REGISTERED_FREE, BASIC_SUB, PRO_SUB, PREMIUM_AI_SUB) with feature limits (max setlists, max songs/setlist, max custom songs, ad-free status, AI assistant access). 2) Create SubscriptionManager to manage user tier and enforce tier limits. 3) Build SubscriptionScreen for upgrading tiers and Ad Banner placeholder for free tiers.
- **Status:** COMPLETED
- **Updates:** Task_84_ImplementMembershipTiersAndSubscriptionManager completed successfully. Implemented 5-Tier Membership System & Subscription Manager: 1) Created UserTier enum (ANONYMOUS, REGISTERED_FREE, BASIC_SUB, PRO_SUB, PREMIUM_AI_SUB) and SubscriptionManager with feature limit enforcement (max setlists, max songs/setlist, max custom songs, ad-free status, AI assistant access). 2) Built SubscriptionScreen.kt and SubscriptionViewModel.kt displaying plan comparisons and upgrade options. 3) Built AdBannerComposable for free tiers (ANONYMOUS & REGISTERED_FREE), automatically hidden for paid tiers. 4) Enforced tier limits in SetlistViewModel and SongEditorViewModel with upgrade dialogs. 5) Added SubscriptionManagerTest unit test suite and localized EN/ES strings. Verified with assembleDebug build and 81 passing unit tests.
- **Acceptance Criteria:**
  - UserTier enum and SubscriptionManager created with tier limits enforcement
  - Setlist creation, custom song creation, and AI access gated according to tier limits
  - SubscriptionScreen UI built for tier management
  - build pass
- **Duration:** 7m 56s

### Task_85_ImplementGeminiAIMusicAssistant: Implement Gemini AI Music Assistant: 1) Build GeminiAiAssistantEngine using Gemini API for guitar & piano instrument learning, chord suggestions, music theory, and app support. 2) Create AiAssistantScreen UI with interactive chat interface. 3) Gate AI assistant access to Premium AI tier or trial.
- **Status:** COMPLETED
- **Updates:** Task_85_ImplementGeminiAIMusicAssistant completed successfully. Implemented Gemini AI Music Assistant: 1) Built GeminiAiAssistantEngine in com.example.songchords.ai tuned for Guitar, Piano, Praise & Worship music theory, chord progressions, transposition, and app usage guidance. 2) Created AiAssistantScreen.kt and AiAssistantViewModel.kt with interactive chat interface, quick prompt suggestion chips, message input, and access gating for PREMIUM_AI_SUB tier users. 3) Integrated AiAssistantRoute and '🤖 IA' tab in Bottom Navigation Bar. 4) Added GeminiAiAssistantEngineTest unit test suite and localized EN/ES strings. Verified with assembleDebug build and 90 passing unit tests.
- **Acceptance Criteria:**
  - GeminiAiAssistantEngine created for music learning & support
  - AiAssistantScreen UI created with interactive chat interface
  - Gated access for Premium AI tier users
  - build pass
- **Duration:** 7m 2s

### Task_86_RunAndVerifySetlistsSubscriptionAndAIAssistant: Verify Transposed Setlists, Subscription Tier Manager, and Gemini AI Assistant on emulator with critic_agent. Ensure zero crashes.
- **Status:** COMPLETED
- **Updates:** Task_86_RunAndVerifySetlistsSubscriptionAndAIAssistant completed successfully. Verified on emulator with 0 crashes. Confirmed all new requested features: 1) Transposed Setlists with custom key overrides (e.g. Original G ➔ Saved in setlist as C). 2) 5 Membership Tiers (Anónimo, Registrado Gratis, Suscripción Básica, Suscripción Pro, Suscripción Premium IA) with tier limit enforcement, SubscriptionScreen, and Ad Banner placeholder. 3) Gemini AI Music Assistant (AiAssistantScreen, GeminiAiAssistantEngine) with interactive chat interface and quick prompt chips for guitar/piano learning, chord progressions, and app support. All 90 unit tests pass.
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - Transposed Setlists, Subscription Manager, and Gemini AI Assistant verified functional on emulator
- **Duration:** 11m 51s

