# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Android
./gradlew :androidApp:assembleDebug

# iOS
# Open iosApp/iosApp.xcodeproj in Xcode and build

# Run tests (shared module)
./gradlew :shared:allTests

# Generate SQLDelight code after modifying .sq files
./gradlew :shared:generateCommonMainAltEarsDatabaseInterface
```

## Architecture

This is a Kotlin Multiplatform (KMP) app with Compose Multiplatform for shared UI across Android and iOS.

### Module Structure
- **shared/** - All business logic and UI (Compose)
- **androidApp/** - Android entry point (thin wrapper that hosts the shared App composable)
- **iosApp/** - iOS entry point (SwiftUI wrapper that embeds shared Compose UI)

### Layers in shared/src/commonMain/
- **data/remote/** - Ktor HTTP client and API models (fetches from `https://api.tmsqr.app/api/v1/festival/455/dashboard`)
- **data/repository/** - FestivalRepository wraps API + SQLDelight database
- **domain/model/** - Domain models (Artist, Show, Venue, etc.)
- **domain/usecase/** - Use cases consumed by ViewModels
- **ui/** - Compose screens and ViewModels organized by feature (artists/, shows/, schedule/, venues/)
- **di/** - Koin dependency injection modules

### Key Patterns
- **ViewModels** use Koin's `viewModelOf()` for DI and expose StateFlow to Compose
- **SQLDelight** schema is in `shared/src/commonMain/sqldelight/com/altears/db/AltEars.sq`
- **Platform-specific code**: Database drivers in `shared/src/androidMain/` and `shared/src/iosMain/` via `expect/actual` for `createDatabase()`
- **Navigation**: Compose Navigation with bottom nav (Artists, Shows, Venues, Schedule) and detail screens (ArtistDetail, VenueDetail)

### Data Flow
1. API response → FestivalRepository.refreshData() → SQLDelight database
2. SQLDelight queries → Flow<List<T>> → Repository → UseCase → ViewModel → Compose UI
3. User schedule (saved shows) persists locally in ScheduleItem table, survives data refresh

### iOS Integration
iOS initializes Koin via `KoinKt.doInitKoin()` in iOSApp.swift, then embeds `MainViewController()` which hosts the shared Compose UI.
