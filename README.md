A cross-platform writing app built with Kotlin Multiplatform and Compose Multiplatform.

## Features

- 15-question personalization quiz
- 10-word prompt generation (6 genres)
- 7-minute focused writing sessions
- 3 canvas styles: Classic, Typewriter, Focus
- Instant feedback with score 3-5
- Offline-first, all data stays local
- Dark/Light theme support

## Technical Stack

- Kotlin Multiplatform (shared business logic)
- Compose Multiplatform (UI)
- SQLDelight (local database)
- StoreKit 2 / Google Play Billing (IAP)
- MVVM architecture

## Implementation

11 screens: Splash, Onboarding, Home, Writing, Result, Search, History, Favorites, Stats, Settings, About

## Build

Android:
```bash
./gradlew :composeApp:installDebug
```

iOS: Open `iosApp/iosApp.xcodeproj` in Xcode
