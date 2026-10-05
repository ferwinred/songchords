# Project Plan

SongChords app - Update SubscriptionScreen to remove Anonymous plan card and present 'Registrado Gratis' requiring Google Sign-In as the entry plan.

## Project Brief

# SongChords - Project Brief

## Features
- **Updated Subscription Tier Hierarchy**: Replaces the anonymous plan card with 'Registrado Gratis' as the baseline entry plan, followed by paid tiers: Suscripción Básica ($2.99), Suscripción Pro ($5.99), and Suscripción Premium IA ($9.99).
- **Google Sign-In Entry Plan Integration**: Presents the 'Registrado Gratis' plan card highlighting usage allowances (3 setlists, max 15 songs/setlist, 2 custom songs) with an integrated Google Sign-In Call-to-Action button for guest users.
- **Subscription Tier Comparison & Status**: Displays plan features, pricing, active subscription status, and dynamic CTA actions across all tiers.
- **Bilingual Localization**: Complete string localization in English and Spanish for all subscription plan details, call-to-actions, and authentication prompts.

## High-Level Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Navigation**: Jetpack Navigation 3 (State-driven)
- **Adaptive Layouts**: Compose Material Adaptive
- **Authentication**: Google Sign-In / Credential Manager API
- **Architecture & State**: Jetpack ViewModel, Kotlin Coroutines, StateFlow
- **Localization**: Android Strings Resources (English & Spanish)

## Implementation Steps
**Total Duration:** 3m 15s

### Task_87_UpdateSubscriptionScreenForGoogleSignInFreePlan: Update SubscriptionScreen.kt and SubscriptionViewModel.kt: 1) Remove ANONYMOUS plan card from plans list in SubscriptionScreen.kt. 2) Display 'Registrado Gratis' (Free Registered) as Plan 1, explicitly noting that it requires Google Sign-In, with 'Registrarse con Google' / 'Iniciar sesión con Google' CTA button for guest users. 3) Keep paid plans (Básica, Pro, Premium IA). 4) Update localized strings in English and Spanish.
- **Status:** COMPLETED
- **Updates:** Task_87_UpdateSubscriptionScreenForGoogleSignInFreePlan completed successfully. Updated SubscriptionScreen.kt and SubscriptionViewModel.kt: 1) Filtered displayPlans = UserTier.entries.filter { it != UserTier.ANONYMOUS } so Anonymous plan card is removed from SubscriptionScreen. 2) Displayed REGISTERED_FREE as Plan 1 with subtitle 'Requiere iniciar sesión con Google' and CTA button 'Iniciar sesión con Google' for anonymous users. 3) Retained paid plans (Básica, Pro, Premium IA). 4) Added requires_google_sign_in and sign_in_with_google localized strings in English and Spanish. Verified with assembleDebug build and 92 passing unit tests.
- **Acceptance Criteria:**
  - Anonymous plan card removed from SubscriptionScreen.kt
  - Plan 1 displays 'Registrado Gratis' requiring Google Sign-in with 'Registrarse con Google' button for guests
  - Paid plans (Básica, Pro, Premium IA) retained
  - build pass

### Task_88_RunAndVerifySubscriptionScreenUpdate: Verify updated SubscriptionScreen without Anonymous card and with Google Sign-in Free Plan on emulator with critic_agent. Ensure zero crashes.
- **Status:** COMPLETED
- **Updates:** Task_88_RunAndVerifySubscriptionScreenUpdate completed successfully. Verified on emulator with 0 crashes. Confirmed updated SubscriptionScreen.kt: 1) Anonymous plan card is completely removed from plans list. 2) Plan 1 is 'Registrado Gratis' with subtitle 'Requiere iniciar sesión con Google' and CTA button 'Iniciar sesión con Google'. 3) Paid plans (Básica, Pro, Premium IA) are present, interactive, and selectable. All 92 unit tests pass cleanly.
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - Updated SubscriptionScreen verified clean on emulator
- **Duration:** 3m 15s

