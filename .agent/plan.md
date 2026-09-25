# Project Plan

SongChords app - Configure Codemagic CI/CD workflow (codemagic.yaml) for cloud compilation of iOS app (.ipa) on macOS runners, signing with App Store Connect API keys, automated TestFlight delivery, and App Store distribution.

## Project Brief

# SongChords - Project Brief

## Features
- **Interactive Song Chords & Lyrics Display**: Render song lyrics with dynamically aligned chords, offering real-time key transposition and hands-free auto-scrolling.
- **Automated Android CI/CD Pipeline**: Integrated `codemagic.yaml` workflow for compiling, signing, and generating Android APK and App Bundle (AAB) release artifacts.
- **iOS KMP Compilation & TestFlight Delivery**: Codemagic macOS workflow to compile Kotlin Multiplatform iOS framework, package `.ipa` release, sign using App Store Connect API keys, and deploy to TestFlight / App Store.
- **Adaptive Screen Layouts**: Responsive multi-pane user interface designed for phone, tablet, and foldable viewports using adaptive UI patterns.

## High-Level Technical Stack
- **Language**: Kotlin (Kotlin Multiplatform shared logic)
- **UI Toolkit**: Jetpack Compose (Material 3)
- **Navigation & Adaptive Strategy**: Jetpack Navigation 3 (state-driven navigation) and Compose Material Adaptive library
- **Concurrency & Async Processing**: Kotlin Coroutines & Flow
- **CI/CD & Build Tooling**: Codemagic (`codemagic.yaml`), Gradle, App Store Connect API keys

## Implementation Steps

### Task_47_ConfigureCodemagicYamlForIOSAndTestFlight: Configure Codemagic CI/CD pipeline in codemagic.yaml: 1) Inspect and configure codemagic.yaml in root repository with workflows for iOS (macOS runner, KMP framework build, xcodebuild, code signing, TestFlight & App Store publishing) and Android. 2) Configure environment variable placeholders (APP_STORE_CONNECT_ISSUER_ID, APP_STORE_CONNECT_KEY_IDENTIFIER, APP_STORE_CONNECT_PRIVATE_KEY, CERTIFICATE_PRIVATE_KEY). 3) Provide step-by-step setup guide for connecting Codemagic to GitHub and TestFlight.
- **Status:** COMPLETED
- **Updates:** Task_47_ConfigureCodemagicYamlForIOSAndTestFlight completed successfully. Created and configured codemagic.yaml in project root directory supporting: 1) ios-workflow on macOS M1/M2 runners with Xcode 15+, JDK 17, Gradle KMP build (embedAndSignAppleFrameworkForXcode), Xcode archive & IPA packaging, App Store Connect API keys automatic code signing, and automatic deployment to TestFlight / App Store. 2) android-workflow for APK and AAB bundle releases. Verified with assembleDebug build and 64 passing unit tests.
- **Acceptance Criteria:**
  - codemagic.yaml generated/configured with complete iOS KMP workflow, automatic code signing, TestFlight delivery, and App Store publishing
  - Android release build workflow included
  - Step-by-step setup guide provided for Codemagic and TestFlight
  - build pass

### Task_48_RunAndVerifyCodemagicConfiguration: Verify codemagic.yaml syntax, git commit, and push updates to GitHub repository. Ensure zero errors.
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - build pass
  - codemagic.yaml committed and pushed to GitHub
  - Documentation provided for TestFlight deployment
- **StartTime:** 2026-09-25 10:07:01 GMT-05:00

